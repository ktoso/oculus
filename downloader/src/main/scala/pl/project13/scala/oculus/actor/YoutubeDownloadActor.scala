
package pl.project13.scala.oculus.actor

import akka.actor.{ActorRef, ActorLogging, Actor}
import pl.project13.scala.oculus.file.DownloadedVideoFile
import java.io.File
import com.google.common.io.Files
import pl.project13.scala.oculus.logging.Logging
import pl.project13.scala.oculus.download.youtube.YoutubeDownloadActions
import pl.project13.scala.oculus.actor.CrawlYoutubePage

class YoutubeDownloadActor(hdfsUploader: ActorRef) extends Actor
  with YoutubeDownloadActions with ActorLogging {

  def receive = {
    case DownloadFromYoutube(url) =>
      log.info("Will download [%s]...".format(url))

      val maybeDownloaded = download(url)
      maybeDownloaded foreach { hdfsUploader ! UploadToHDFS(_) }

      log.info("Finished downloading [%s], will request crawling it!".format(url))

      sender ! CrawlYoutubePage(url)
  }
}
