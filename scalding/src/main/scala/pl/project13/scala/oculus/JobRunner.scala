package pl.project13.scala.oculus

import pl.project13.scala.oculus.job._
import com.twitter.scalding
import org.apache.hadoop.conf.Configuration
import collection.JavaConversions._
import com.typesafe.config.{ConfigFactory, Config}
import org.apache.hadoop.fs.{Path, FileSystem}
import com.google.common.base.Stopwatch
import com.twitter.scalding._
import com.twitter.scalding.Hdfs
import org.apache.hadoop
import scala.util.{Failure, Success}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object JobRunner extends App with OculusJobs {

  import pl.project13.scala.rainbow._

  var i = 0
  def inc = { val j = i; i += 1; j }

  val availableJobs =
    (inc, "hash all files", hashAllSequenceFiles _) ::
    (inc, "hash one file", hashSequenceFile _) ::
    (inc, "compare two movies", compareTwoMovies _) ::
    (inc, "find movies similar to given", findSimilarToGiven _) ::
    (inc, "find movies similar to given, v2", findSimilarToGivenV2 _) ::
    (inc, "extract text from movie", extractText _) ::
    Nil

  val availableJobsString = availableJobs.map(d => "  " + d._1 + ") " + d._2).mkString("\n")
  println("Available jobs to run: \n".bold + availableJobsString)
  println("Please run with [id] of task you want to execute.")

  if (args.isEmpty) {
    println("no arguments, exiting.")
    System.exit(-1)
  }

  val selected = availableJobs.drop(args(0).toInt).head

  val task = selected._3

  task(args.tail)

}

trait OculusJobs {
  import pl.project13.scala.rainbow._

  import scala.concurrent.ExecutionContext.Implicits.global


  private val conf = new Configuration()

  /** Override if you need other than default settings - loads up ''application.conf'' */
  def appConfig: Config = ConfigFactory.load()

  conf.setStrings("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem")
  allOculusHadoopSettings(appConfig) foreach { case (key, value) =>
    conf.setStrings(key, value.unwrapped.toString)
  }


  def hashAllSequenceFiles(args: Seq[String]) = {
    val fs = FileSystem.get(conf)
    val seqFiles = fs.listStatus(new Path("hdfs:///oculus/source/")).map(_.getPath.toString)

    println(s"Found [${seqFiles.length}] sequence files. Will hash all of them.".green)

    val totalStopwatch = (new Stopwatch).start()

    val jobClass = classOf[HashHistVideoSeqFilesJob]
    val jobClassName = jobClass.getCanonicalName

    val jobFutures = seqFiles map { seq =>
      println(s"Starting execution of jobs for $seq ...".green)
      val stopwatch = (new Stopwatch).start()

      val allArgs = List(
        jobClassName,
        "--hdfs", IPs.HadoopMasterWithPort,
        "--input", seq
      ) ++ args.toList

      println("-----------------------------------".bold)
      println(("allArgs = " + allArgs).bold)
      println("-----------------------------------".bold)

      HadoopProcessRunner(allArgs).runAndWait(conf)

      println(s"Finished running scalding job for [$seq}]. Took ${stopwatch.stop()}".green)
    }

    println(s"Finished running all jobs. Took ${totalStopwatch.stop()}".green)
  }

  def hashSequenceFile(args: Seq[String]) =
    simpleHadoopRun(args, classOf[HashHistVideoSeqFilesJob])

  def extractText(args: Seq[String]) = {
    simpleHadoopRun(args, classOf[ExtractTextFromMovieJob])
  }

  def compareTwoMovies(args: Seq[String]) = {
    simpleHadoopRun(args, classOf[CompareTwoMoviesJob])
  }
  
  def findSimilarToGiven(args: Seq[String]) = {
    simpleHadoopRun(args, classOf[FindSimilarMoviesJob])
  }

  def findSimilarToGivenV2(args: Seq[String]) = {
    simpleHadoopRun(args, classOf[FindSimilarMoviesV2Job])
  }

  def simpleHadoopRun(args: Seq[String], jobClazz: Class[_]) {
    val totalStopwatch = (new Stopwatch).start()

    val jobClassName = jobClazz.getCanonicalName

    println(s"Starting execution of job ${jobClassName.bold} ...".green)

    val allArgs = List(
      jobClassName,
      "--hdfs", IPs.HadoopMasterWithPort
    ) ++ args.toList

    println("-----------------------------------".bold)
    println(("args = " + allArgs.toList).bold)
    println("-----------------------------------".bold)

    HadoopProcessRunner(allArgs.toList).runAndWait(conf)

    println(s"Finished running all jobs. Took ${totalStopwatch.stop()}".green)
  }

  private def allOculusHadoopSettings(configuration: com.typesafe.config.Config) =
    configuration.getConfig("oculus").getConfig("hadoop").entrySet().toList.map(a => (a.getKey, a.getValue))
}