package pl.project13.scala.oculus

import actor._
import akka.actor.{Props, ActorSystem}
import akka.routing.{FromConfig, RoundRobinRouter}
import com.typesafe.config.ConfigFactory

object DownloaderRunner extends App {

  // config
  val DownloaderNumber = 5
  // end of config


  val config = ConfigFactory.load()
  val system = ActorSystem("oculus-system", config)

  val hdfsUploader = system.actorOf(Props[HDFSUploadActor].withRouter(FromConfig), "hdfs-uploader")
  val youtubeDownloader = system.actorOf(Props(new YoutubeDownloadActor(hdfsUploader)).withRouter(FromConfig), "youtube-downloader")
  val youtubeCrawler = system.actorOf(Props(new YoutubeCrawlActor(youtubeDownloader)), "youtube-crawler")

//  youtubeDownloader ! DownloadFromYoutube("http://www.youtube.com/watch?v=fFK_YCS8ab0&list=WLFDF76FA8065675A9")

//  youtubeCrawler ! CrawlYoutubePage("http://www.youtube.com/watch?v=fFK_YCS8ab0&list=WLFDF76FA8065675A9") // vocaloids
  youtubeCrawler ! CrawlYoutubePage("http://www.youtube.com/watch?v=uAcD7H53220") // spiderman
  youtubeCrawler ! CrawlYoutubePage("http://www.youtube.com/watch?v=GAkw_Wi4yIo") // tchaikovsky
//  youtubeCrawler ! CrawlYoutubePage("http://www.youtube.com/watch?v=hO6eQnH41ao") // project retouch, landscape

}
