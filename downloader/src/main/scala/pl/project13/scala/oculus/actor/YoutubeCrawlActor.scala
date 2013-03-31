
package pl.project13.scala.oculus.actor

import akka.actor.{ActorRef, ActorLogging, Actor}
import pl.project13.scala.oculus.download.youtube.YoutubeCrawlActions

class YoutubeCrawlActor(downloadActor: ActorRef) extends Actor
  with YoutubeCrawlActions with ActorLogging {

  def receive = {
    case CrawlYoutubePage(url) =>
      val page = fetchPage(url)
      val urls = extractVideoUrls(page)

      urls foreach { url => downloadActor ! DownloadFromYoutube(url) }
  }
}