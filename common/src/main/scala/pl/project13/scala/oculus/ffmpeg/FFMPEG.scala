package pl.project13.scala.oculus.ffmpeg

import java.io.{FilenameFilter, File}
import pl.project13.scala.oculus.logging.Logging
import com.google.common.io.Files
import pl.project13.common.utils.HumanReadable
import scala.sys.process.Process

object FFMPEG extends Logging {

  def ffmpegToImages(video: File, framesPerSecond: Int): List[File] = {

    // we'll work in an isolated tmp directory
    val tmp = Files.createTempDir()
    val sourceFile = new File(tmp, video.getName)
    Files.copy(video, sourceFile)

    logger.info("We will extract [%s] frames per second from the [%s] file, starting ffmpeg".format(framesPerSecond, sourceFile))
    val ffmpeg = ffmpgCommand(InputFile(sourceFile), FramesPerSecond(framesPerSecond), OutputFormatPngImage2, OutputFilePngNamePattern)

    Process(ffmpeg, sourceFile.getParentFile).!

    sourceFile.delete()
    logger.info("Finished ffmpeg run, deleted [%s]".format(sourceFile))

    val generatedImages = tmp.listFiles(onlyImages).toList
    val totalByteCount = HumanReadable.byteCount(generatedImages.map(_.length).sum)
    logger.info("Generated [%s] images, totaling: %s".format(generatedImages.size, totalByteCount))

    generatedImages
  }

  private val onlyImages = new FilenameFilter {
    def accept(dir: File, name: String) = name endsWith "png"
  }

  private def ffmpgCommand(options: FFMPEGOption*): String = {
    val command = "ffmpeg " + options.mkString(" ")
    logger.info("ffmpeg command: " + command)
    command
  }
}
