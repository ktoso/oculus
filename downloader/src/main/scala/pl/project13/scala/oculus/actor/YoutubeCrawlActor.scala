
package pl.project13.scala.oculus.actor

import akka.actor.{ActorRef, ActorLogging, Actor}
import java.net.{URL, URI}
import akka.event.LoggingAdapter
import org.jsoup.Jsoup
import scala.collection.JavaConversions._

class YoutubeCrawlActor(downloadActor: ActorRef) extends Actor
  with YoutubeCrawlActions with ActorLogging {

  def receive = {
    case CrawlYoutubePage(url) =>
      val page = fetchPage(url)
      val urls = extractVideoUrls(page)

      urls foreach { url => downloadActor ! DownloadFromYoutube(url) }
  }
}

trait YoutubeCrawlActions {
  def log: LoggingAdapter

  val YoutubeVideoLink = """<a href="/watch([\d0-9]+)" """.r

  def fetchPage(url: String): String = {
    log.info("Fetching page [%s]...".format(url))

    val source = io.Source.fromURL(new URL(url))
    val wholePage = source.getLines().mkString("\n")
    source.close()

    wholePage
  }

  def extractVideoUrls(page: String): List[String] = {
    val doc = Jsoup.parse(page)
    val links = doc.select("a[href^=/watch]")

    val urls = links map { _.attr("href") } map { "http://www.youtube.com" + _ }

    urls foreach { u => log.info("Found url to follow and download [%s]...".format(u)) }
    urls.toList
  }
}