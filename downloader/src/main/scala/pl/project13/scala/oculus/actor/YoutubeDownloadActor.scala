
package pl.project13.scala.oculus.actor

import akka.actor.{ActorRef, ActorLogging, Actor}
import pl.project13.scala.oculus.download.youtube.YoutubeDownloadActions

class YoutubeDownloadActor(hdfsUploader: ActorRef) extends Actor
  with YoutubeDownloadActions with ActorLogging {

  def receive = {
    case DownloadFromYoutube(url) =>
      log.info("Will download [%s]...".format(url))

      val maybeDownloaded = downloadYoutubeVideo(url)
      maybeDownloaded foreach { hdfsUploader ! RequestUploadToHDFS(_) }

      log.info("Finished downloading [%s], will request crawling it!".format(url))

      sender ! CrawlYoutubePage(url)
  }
}
