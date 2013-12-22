package pl.project13.scala.oculus

import pl.project13.scala.oculus.job.{HashVideoSeqFilesJob, WordCountJob}
import org.apache.hadoop.util._
import com.twitter.scalding
import org.apache.hadoop.conf.Configuration
import collection.JavaConversions._
import com.typesafe.config.{ConfigFactory, Config}
import org.apache.hadoop.fs.{Path, FileSystem, FileUtil}
import java.io.File
import com.google.common.base.Stopwatch
import com.twitter.scalding.{Job, Hdfs, Args, Mode}
import org.apache.hadoop

object JobRunner extends App with OculusJobs {

  import pl.project13.scala.rainbow._

  val availableJobs =
    (0, "hash all files", hashAllSequenceFiles _) ::
    Nil

  val availableJobsString = availableJobs.map(d => "  " + d._1 + ") " + d._2).mkString("\n")
  println("Available jobs to run: \n".bold + availableJobsString)
  println("Please run with [id] of task you want to execute.")

  val selected = availableJobs.drop(args(0).toInt).head

  val task = selected._3

  task()

}

trait OculusJobs {
  import pl.project13.scala.rainbow._

  private val conf = new Configuration(false)
  private val tool = new scalding.Tool

  /** Override if you need other than default settings - loads up ''application.conf'' */
  def appConfig: Config = ConfigFactory.load()

  conf.setStrings("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem")
  allOculusHadoopSettings(appConfig) foreach { case (key, value) =>
    conf.setStrings(key, value.unwrapped.toString)
  }


  def hashAllSequenceFiles() = {
    val fs = FileSystem.get(conf)
    val seqFiles = fs.listStatus(new Path("hdfs:///oculus/source/")).map(_.getPath.toString)

    println(s"Found [${seqFiles.length}] sequence files. Will hash all of them.".green)

    val totalStopwatch = (new Stopwatch).start()

    val jobClass: Class[HashVideoSeqFilesJob] = classOf[HashVideoSeqFilesJob]
    val jobClassName = jobClass.getCanonicalName

    for(seq <- seqFiles) {
      val stopwatch = (new Stopwatch).start()

      val allArgs = Array(
        jobClassName,
        "--hdfs", IPs.HadoopMasterWithPort,
        "--input", seq
      )

      tool.setConf(conf)
      val (mode, args) = tool.parseModeArgs(allArgs)

      conf.setClass("cascading.app.appjar.class", jobClass, classOf[Job])

      println("-----------------------------------")
      println("allArgs = " + allArgs.toList)
      println("mode = " + mode)
      println("args = " + args)
      println("cascading.app.appjar.class = " + jobClassName)
      println("-----------------------------------")

      Mode.mode = Hdfs(strict = true, conf)
      println("Mode.mode set manualy = " + Mode.mode)

      hadoop.util.ToolRunner.run(conf, tool, allArgs)
      println(s"Finished running scalding job for [$seq}]. Took ${stopwatch.stop()}".green)
    }

    println(s"Finished running all jobs. Took ${totalStopwatch.stop()}".green)
  }





    private def allOculusHadoopSettings(configuration: com.typesafe.config.Config) =
      configuration.getConfig("oculus").getConfig("hadoop").entrySet().toList.map(a => (a.getKey, a.getValue))
}