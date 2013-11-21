package pl.project13.scala.oculus.download.youtube

import java.net.URL
import org.jsoup.Jsoup
import scala.collection.JavaConversions._
import com.typesafe.scalalogging.slf4j.Logging
import scala.sys.process.Process

trait YoutubeCrawlActions extends Logging {

  val YoutubeVideoLink = """<a href="/watch([\d0-9]+)" """.r

  def fetchPage(url: String): String = {
    logger.info("Fetching page [%s]...".format(url))

    Process("curl url").!!
  }

  def extractVideoUrls(page: String): List[String] = {
    val doc = Jsoup.parse(page)
    val links = doc.select("a[href^=/watch]")

    val urls = links map { _.attr("href") } map { "http://www.youtube.com" + _ }

    urls foreach { u => logger.info("Found url to follow and download [%s]...".format(u)) }
    urls.toList
  }
}
