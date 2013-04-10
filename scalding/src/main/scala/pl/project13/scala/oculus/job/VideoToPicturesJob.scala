package pl.project13.scala.oculus.job

import com.twitter.scalding._
import java.io.File
import pl.project13.scala.oculus.ffmpeg.FFMPEG
import com.google.common.io.Files
import pl.project13.scala.oculus.logging.Logging
import com.typesafe.config.{ConfigFactory, Config}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{Path, FileSystem}
import collection.JavaConversions._

class VideoToPicturesJob(args: Args) extends Job(args) with HDFSActions {

  val input = args("input")
  val output = args("output")

//  TextLine(input)
////    .flatMap('line -> 'image) { video: String => convert(asLocalFile(inputFile)) }
////    .groupAll
//    .write(SequenceFile(output))
//

// convert a normal file - the movie, into a seq file, with only on seq element
  TextLine(input)
    .write(SequenceFile(output))

//  SequenceFile(input).read
//    .flatMap('* -> 'other) { all: Int => convert(saveLocally(Nil.toArray)) }
//    .write(SequenceFile(output))

  def convert(video: File): List[File] =
    FFMPEG.ffmpegToImages(video, framesPerSecond = 1)

  // todo should work on HDFS only
  def saveLocally(video: Array[Byte]): File = {
    val file = File.createTempFile("prefix", "suffix")
//    fileSystem.copyToLocalFile(new Path(input), new Path(file.getAbsolutePath))

    println("video.size = " + video.size)
    println("file = " + file)
    println("file = " + file)
    println("file = " + file)
    println("file = " + file)
    Files.write(video, new File(input))

    file
  }

}



trait HDFSActions {

  /** Override if you need other than default settings - loads up ''application.conf'' */
  def configuration: Config = ConfigFactory.load()

  val requireDistributedMode = true

  lazy val hdfsConfiguration = {
    val conf = new Configuration(true)

    allOculusHadoopSettings(configuration) foreach { case (key, value) =>

      conf.setStrings(key, value.unwrapped.toString)
    }

    conf
  }

  lazy val fileSystem = {
    val fs = FileSystem.get(hdfsConfiguration)

    Runtime.getRuntime.addShutdownHook(new Thread(){
      override def run() {
        fs.close()
      }
    })

    fs
  }

  def uploadAsSequenceFile(localPath: File, hdfsTarget: String, delSrc: Boolean = true, overwrite: Boolean = true) = {

  }

  /**
   * @return Path referncing the uploaded file (on HDFS)
   */
  def upload(localPath: File, hdfsTarget: String, delSrc: Boolean = true, overwrite: Boolean = true) = {

    val from = new Path(localPath.getAbsolutePath)
    val to = new Path(hdfsTarget)

    fileSystem.copyFromLocalFile(delSrc, overwrite, from, to)

    to
  }

  def delete(path: String) = {
    fileSystem.delete(new Path(path), true)
  }

  private def allOculusHadoopSettings(configuration: com.typesafe.config.Config) =
    configuration.getConfig("oculus").getConfig("hadoop").entrySet().toList.map(a => (a.getKey, a.getValue))

}
