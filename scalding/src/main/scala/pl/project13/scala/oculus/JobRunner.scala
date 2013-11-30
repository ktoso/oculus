package pl.project13.scala.oculus

import pl.project13.scala.oculus.job.{HashVideoSeqFilesJob, WordCountJob}
import org.apache.hadoop.util._
import com.twitter.scalding
import org.apache.hadoop.conf.Configuration
import collection.JavaConversions._
import com.typesafe.config.{ConfigFactory, Config}
import org.apache.hadoop.fs.FileUtil
import java.io.File

object JobRunner extends App with OculusJobs {

  import pl.project13.scala.rainbow._

  val availableJobs =
    (0, "hash all files", hashAllSequenceFiles _) ::
    Nil

  val availableJobsString = availableJobs.map(d => "  " + d._1 + ") " + d._2).mkString("\n")
  println("Available jobs to run: \n".bold + availableJobsString)
  println("Please run with [id] of task you want to execute.")

  val selected = availableJobs.drop(args(0).toInt).head

  /** Override if you need other than default settings - loads up ''application.conf'' */
  def appConfig: Config = ConfigFactory.load()

  val task = selected._3

  task()

}

trait OculusJobs {
  import pl.project13.scala.rainbow._

  private val conf = new Configuration
  private val tool = new scalding.Tool

  /** Override if you need other than default settings - loads up ''application.conf'' */
  def appConfig: Config = ConfigFactory.load()

  conf.setStrings("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem")
  allOculusHadoopSettings(appConfig) foreach { case (key, value) =>
    conf.setStrings(key, value.unwrapped.toString)
  }


  def hashAllSequenceFiles() = {
    val seqFiles = FileUtil.list(new File("/oculus/source/")) filter { _.endsWith(".seq") }

    println(s"Found [${seqFiles.length}] sequence files. Will hash all of them.".green)

    for(seq <- seqFiles) {
      val allArgs = Array(
        classOf[HashVideoSeqFilesJob].getCanonicalName,
        "--input", seq,
        "--hdfs", IPs.HadoopMasterWithPort
      )

      println("-----------------------------------")
      println("allArgs = " + allArgs.toList)
      println("-----------------------------------")

      ToolRunner.run(conf, tool, allArgs)
      println(s"Finished running scalding job for [$seq}]".green)
    }
  }





    private def allOculusHadoopSettings(configuration: com.typesafe.config.Config) =
      configuration.getConfig("oculus").getConfig("hadoop").entrySet().toList.map(a => (a.getKey, a.getValue))
}