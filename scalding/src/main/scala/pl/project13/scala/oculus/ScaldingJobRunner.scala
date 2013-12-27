package pl.project13.scala.oculus

import pl.project13.scala.oculus.job._
import org.apache.hadoop.util._
import com.twitter.scalding
import org.apache.hadoop.conf.Configuration
import collection.JavaConversions._
import com.typesafe.config.{ConfigFactory, Config}
import com.twitter.scalding.Mode
import org.apache.hadoop
import pl.project13.hadoop.NoJarTool
import java.io.File

object ScaldingJobRunner extends App {

  import pl.project13.scala.rainbow._

  val availableJobs =
    classOf[HashVideoSeqFilesJob] ::
    classOf[CompareTwoMoviesJob] ::
    classOf[HistogramSeqFilesJob] ::
    classOf[FindSimilarMoviesJob] ::
    classOf[WordCountJob] ::
    Nil

  val availableJobsString = availableJobs.map("  " + _.getCanonicalName).mkString("\n").bold.greenIf(args.headOption.getOrElse(""))
  println("Available jobs to run: \n".bold + availableJobsString)
  println()

  args foreach println

  val conf = new Configuration

  /** Override if you need other than default settings - loads up ''application.conf'' */
  val appConfig: Config = ConfigFactory.load()

  val projHome = "/home/kmalawski/oculus/scalding"
  val tool = new NoJarTool(new scalding.Tool)

  allOculusHadoopSettings(appConfig) foreach { case (key, value) =>
    conf.setStrings(key, value.unwrapped.toString)
  }

  val allArgs = args ++ Array("--hdfs", IPs.HadoopMasterWithPort)
  println("-----------------------------------")
  println("allArgs = " + allArgs.toList)
  println("-----------------------------------")


  ToolRunner.run(conf, tool, allArgs)

  println("Mode.mode = " + Mode.mode)


  private def allOculusHadoopSettings(configuration: com.typesafe.config.Config) =
    configuration.getConfig("oculus").getConfig("hadoop").entrySet().toList.map(a => (a.getKey, a.getValue))
}
