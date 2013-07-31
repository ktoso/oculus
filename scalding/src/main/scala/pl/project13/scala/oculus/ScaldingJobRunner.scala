package pl.project13.scala.oculus

import pl.project13.scala.oculus.job.{HashVideoSeqFilesJob, VideoToPicturesJob, WordCountJob}
import org.apache.hadoop.util._
import com.twitter.scalding
import org.apache.hadoop.conf.Configuration

object ScaldingJobRunner extends App {

  import pl.project13.scala.rainbow._

  val availableJobs =
    classOf[HashVideoSeqFilesJob] ::
    classOf[WordCountJob] ::
    Nil

  val availableJobsString = availableJobs.map("  " + _.getCanonicalName).mkString("\n").bold.greenIf(args.headOption.getOrElse(""))
  println("Available jobs to run: \n".bold + availableJobsString)
  println()

  ToolRunner.run(new Configuration, new scalding.Tool, args)

}
