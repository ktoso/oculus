package pl.project13.scala.oculus

import java.io.File
import scala.sys.process._

case class HadoopProcessRunner(args: List[String]) {

  val hadoopBin = findHadoopBin()

  def runAndWait() = {
    val process = Process(hadoopBin :: "jar" :: locateOculusScaldingJar :: args)

    process.!(ProcessLogger(l => println("hadoop: " + l)))
  }


  private def locateOculusScaldingJar(): String = {
    val maybeHere =
      "/home/kmalawski/oculus/scalding/target/scalding-1.0.0.jar" ::
      "/Users/kmalawski/code/oculus/scalding/target/scalding-1.0.0.jar" ::
      Nil

    maybeHere map { new File(_) } find {
      _.exists()
    } map {
      _.getAbsolutePath
    } getOrElse {
      throw new RuntimeException("Please compile the oculus-scalding jar! (`assembly` in sbt)")
    }
  }

  private def findHadoopBin(): String = {
    val maybeHere =
      "/opt/hadoop-1.2.1/bin/hadoop" ::
      "/Users/kmalawski/code/hadoop-1.2.1/bin/hadoop" ::
      "/opt/local/share/java/hadoop-1.2.1/bin/hadoop" ::
      Nil

    maybeHere map { new File(_) } find {
      _.exists()
    } map {
      _.getAbsolutePath
    } getOrElse {
      throw new RuntimeException("Unable to find hadoop executable")
    }
  }

}
