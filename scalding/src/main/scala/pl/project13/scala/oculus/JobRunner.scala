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
import org.apache.hadoop.hbase.client.Delete

object JobRunner extends App with OculusJobs {

  import pl.project13.scala.rainbow._

  val availableJobs =
    (0,   "hash all files", hashAllSequenceFiles _) ::
    (1,   "hash one file", hashSequenceFile _) ::
    (2,   "naive compare two movies", compareTwoMovies _) ::
    (3,   "find movies similar to given", findSimilarToGiven _) ::
    (4,   "find movies similar to given, v2", findSimilarToGivenV2 _) ::
    (5,   "list available files", listAvailableFiles _) ::
    (6,   "extract text from movie", extractText _) ::
    (7,   "delete all data about [id]", deleteAllDataAboutMovieWithId _) ::
    (8,   "histo compare two movies", histoCompareTwoMovies _) ::
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

    val jobClass = classOf[HashHistVideoSeqFileJob]
    val jobClassName = jobClass.getCanonicalName

    seqFiles map { seq =>
      println(s"Starting execution of jobs for $seq ...".green)
      val stopwatch = (new Stopwatch).start()

      val allArgs = hdfsOptionsIfNoneGiven(args.toList) ++ List(
        jobClassName,
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
    simpleHadoopRun(args, classOf[HashHistVideoSeqFileJob])

  def extractText(args: Seq[String]) =
    simpleHadoopRun(args, classOf[ExtractTextFromMovieJob])

  def compareTwoMovies(args: Seq[String]) =
    simpleHadoopRun(args, classOf[CompareTwoMoviesJob])

  def histoCompareTwoMovies(args: Seq[String]) =
    simpleHadoopRun(args, classOf[HistoCompareTwoMoviesJob])

  def findSimilarToGiven(args: Seq[String]) =
    simpleHadoopRun(args, classOf[FindSimilarMoviesJob])

  def findSimilarToGivenV2(args: Seq[String]) =
    simpleHadoopRun(args, classOf[FindSimilarMoviesV2Job])

  def listAvailableFiles(args: Seq[String]) = {
    val fs = FileSystem.get(conf)
    fs.listStatus(new Path("hdfs:///oculus/source/")).map(_.getPath.toString) foreach { it =>
      println("file: " + it)
    }
  }

  def deleteAllDataAboutMovieWithId(args: Seq[String]) =
    simpleHadoopRun(args, classOf[DeleteAllDataAboutGivenIdJob])

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

  def hdfsOptionsIfNoneGiven(args: List[String]): List[String] =
    if (args.contains("--hdfs") || args.contains("--local")) Nil
    else List("--hdfs", IPs.HadoopMasterWithPort)

  private def allOculusHadoopSettings(configuration: com.typesafe.config.Config) =
    configuration.getConfig("oculus").getConfig("hadoop").entrySet().toList.map(a => (a.getKey, a.getValue))
}