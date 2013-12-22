package pl.project13.scala.oculus

import java.io.File
import scala.sys.process._

case class HadoopProcessRunner(args: List[String]) {

  val hadoopBin = "/opt/hadoop-1.2.1/bin/hadoop"

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

}

import collection.JavaConversions._

class AwaitForStringProcess(commandParts: List[String]) {

  def runAndAwaitFor(msg: String): EnhancedUNIXProcess = {
    val process = new java.lang.ProcessBuilder(commandParts).start()
    waitToStart(process, msg)
    process
  }

  private def waitToStart(process: java.lang.Process, awaitMsg: String): Unit = {
    val is = process.getInputStream
    val lines = io.Source.fromInputStream(is).getLines()
    var line = lines.next()
    while(!line.contains(awaitMsg)) {
      if (!line.trim.isEmpty)
        println("> " + line)
      if(lines.isEmpty)
        return ()
      line = lines.next()
    }
  }
}

object AwaitForStringProcess {
  implicit final def asAwaitForStringProcess(command: List[String]): AwaitForStringProcess = new AwaitForStringProcess(command)
}

class EnhancedUNIXProcess(process: Process) {

  /** Gets the PID of an UNIX process, via an awful hack. */
  def pidOrThrow: Int =
    pid getOrElse { throw new RuntimeException("Unable to obtain PID for %s".format(process)) }

  /** Gets the PID of an UNIX process, via an awful hack. */
  def pid: Option[Int] = try {
    val clazz = process.getClass

    val field = clazz.getDeclaredField("pid")
    field.setAccessible(true)
    val pid = field.getInt(process)
    field.setAccessible(false)

    Some(pid)
  } catch {
    case ex: Exception => None
  }

  def kill() = {
    import scala.sys.process._
    """kill %s""".format(pidOrThrow).!
  }

}

object EnhancedUNIXProcess {
  implicit final def asEnhancedUNIXProcess(p: java.lang.Process): EnhancedUNIXProcess = new EnhancedUNIXProcess(p)
}
