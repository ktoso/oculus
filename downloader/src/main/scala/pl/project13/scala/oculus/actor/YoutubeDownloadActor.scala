
package pl.project13.scala.oculus.actor

import akka.actor.{ActorRef, ActorLogging, Actor}
import pl.project13.scala.oculus.download.youtube.YoutubeDownloadActions

class YoutubeDownloadActor(hdfsUploader: ActorRef) extends Actor
  with YoutubeDownloadActions with ActorLogging {

  def receive = {
    case m: DownloadFromYoutube =>
      log.info("Will download [%s]...".format(m.url))

      downloadYoutubeVideo(m.url) foreach { downloaded =>
        hdfsUploader ! RequestUploadToHDFS(downloaded)

        log.info("Finished downloading [%s], will request crawling it!".format(m.url))

        if(m.crawl)
          sender ! m
      }
  }
}
