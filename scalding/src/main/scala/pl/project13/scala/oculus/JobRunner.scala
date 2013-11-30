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
  def hashAllSequenceFiles() = {
    val seqFiles = FileUtil.list(new File("/oculus/source/")) filter { _.endsWith(".seq") }

    println(s"Found [${seqFiles.length}] sequence files. Will hash all of them.")

    for(seq <- seqFiles)
      ScaldingJobRunner.main(Array(
        "--input", seq
      ))
  }
}