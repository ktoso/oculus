package pl.project13.scala.oculus

import actor._
import akka.actor.{Props, ActorSystem}

object DownloaderRunner extends App {

  val system = ActorSystem("oculus-system")

  val youtubeDownloader = system.actorOf(Props[YoutubeDownloadActor], "youtube-downloader")
  val youtubeCrawler = system.actorOf(Props(new YoutubeCrawlActor(youtubeDownloader)), "youtube-crawler")

//  youtubeDownloader ! DownloadFromYoutube("http://www.youtube.com/watch?v=fFK_YCS8ab0&list=WLFDF76FA8065675A9")

//  youtubeCrawler ! CrawlYoutubePage("http://www.youtube.com/watch?v=fFK_YCS8ab0&list=WLFDF76FA8065675A9") // vocaloids
//  youtubeCrawler ! CrawlYoutubePage("http://www.youtube.com/watch?v=uAcD7H53220") // spiderman
  youtubeCrawler ! CrawlYoutubePage("http://www.youtube.com/watch?v=hO6eQnH41ao") // project retouch, landscape

}
