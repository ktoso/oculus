
package pl.project13.scala.oculus.actor

import akka.actor.{ActorRef, ActorLogging, Actor}
import pl.project13.scala.oculus.download.youtube.YoutubeCrawlActions

class YoutubeCrawlActor(downloadActor: ActorRef) extends Actor
  with YoutubeCrawlActions with ActorLogging {

  def receive = {
    case m: CrawlYoutubePage =>
      val page = fetchPage(m.url)
      val urls = extractVideoUrls(page)

      urls foreach { url => downloadActor ! m.copy(url = url) }

    case m: DownloadFromYoutube =>
      log.info(s"Forwarding request to download one video: ${m.url}")
      downloadActor ! m
  }
}