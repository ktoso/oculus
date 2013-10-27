package pl.project13.scala.oculus.download.youtube

import java.io.File
import com.google.common.io.Files
import pl.project13.scala.oculus.file.DownloadedVideoFile
import com.typesafe.scalalogging.slf4j.Logging

trait YoutubeDownloadActions extends Logging {

  val OutputFilename = """.download. Destination: (.*)\.(.*)""".r

  val baseDir = {
    val f = new File("/data/youtube")
    Files.createParentDirs(f)
    f
  }

  import scala.sys.process._

  def downloadYoutubeVideo(url: String): Option[DownloadedVideoFile] = {
    val command = {
      "youtube-dl" ::
        "--no-progress" ::
        "--continue" ::
        "--write-info-json" ::
        "--id" ::
        "--restrict-filenames" ::
        "--prefer-free-formats" ::
        url :: Nil
    } mkString " "

    try {
      val output = Process(command, baseDir).!!

      OutputFilename.findFirstMatchIn(output) map { m =>
        DownloadedVideoFile(baseDir, m.group(1), m.group(2))
      }
    } catch {
      case ex: Exception if ex.getMessage contains "has blocked it in your country on copyright grounds" =>
        logger.warn("Unable to download [%s] as if was blocked. Details: %s".format(url, ex.getMessage))
        None

      case ex: Exception =>
        logger.error("Unable to download youtube movie [%s]!".format(url))
        None
    }
  }
}