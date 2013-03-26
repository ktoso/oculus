
package pl.project13.scala.oculus.actor

import akka.actor.{ActorRef, ActorLogging, Actor}
import pl.project13.scala.oculus.file.DownloadedVideoFile
import java.io.File
import com.google.common.io.Files

class YoutubeDownloadActor(hdfsUploader: ActorRef) extends Actor
with YoutubeDownloadActions with ActorLogging {

  def receive = {
    case DownloadFromYoutube(url) =>
      log.info("Will download [%s]...".format(url))

      val maybeDownloaded = download(url)
      maybeDownloaded foreach { hdfsUploader ! UploadToHDFS(_) }

      log.info("Finished downloading [%s]!".format(url))
  }
}

trait YoutubeDownloadActions {

  val OutputFilename = """.download. Destination: (.*)\.(.*)""".r

  val baseDir = {
    val f = new File("/data/youtube")
    Files.createParentDirs(f)
    f
  }

  import scala.sys.process._

  def download(url: String): Option[DownloadedVideoFile] = {
    val command = {
      "youtube-dl" ::
        "--no-progress" ::
        "--continue" ::
        "--title" ::
        "--write-info-json" ::
        "--prefer-free-formats" ::
        url :: Nil
    } mkString " "

    val output = Process(command, baseDir).!!

    OutputFilename.findFirstMatchIn(output) map { m =>
      DownloadedVideoFile(baseDir, m.group(1), m.group(2))
    }
  }
}