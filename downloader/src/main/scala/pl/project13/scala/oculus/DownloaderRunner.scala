package pl.project13.scala.oculus

import actor._
import akka.actor.{Props, ActorSystem}
import akka.routing.{FromConfig, RoundRobinRouter}
import com.typesafe.config.ConfigFactory
import pl.project13.scala.oculus.hbase.HBaseInit

object DownloaderRunner extends App {

  // config
  val DownloaderNumber = 3
  // end of config

  HBaseInit.init()

  val config = ConfigFactory.load()
  val system = ActorSystem("oculus-system", config)

  val fs = config.getString("oculus.hadoop.fs.defaultFS")
  val fps = config.getInt("oculus.ffmpeg.framesPerSecond")

  val hdfsUploader = system.actorOf(Props(new HDFSUploadActor(fs, fps)).withRouter(FromConfig), "hdfs-uploader")
  val youtubeDownloader = system.actorOf(Props(new YoutubeDownloadActor(hdfsUploader)).withRouter(FromConfig), "youtube-downloader")
  val youtubeCrawler = system.actorOf(Props(new YoutubeCrawlActor(youtubeDownloader)), "youtube-crawler")

  // download specific videos
//  youtubeDownloader ! DownloadFromYoutube("http://www.youtube.com/watch?v=fFK_YCS8ab0&list=WLFDF76FA8065675A9", crawl = false)

//  youtubeDownloader ! DownloadFromYoutube(PreppedMovieUrls.Mirrored.AttackOnTitan, crawl = true)
//  youtubeDownloader ! DownloadFromYoutube(PreppedMovieUrls.Original.AttackOnTitan, crawl = true)
//
//  youtubeCrawler ! CrawlYoutubePage(PreppedMovieUrls.Original.AttackOnTitan)
//  youtubeCrawler ! CrawlYoutubePage(PreppedMovieUrls.Original.VocaloidIARevengeSyndrome)
//  youtubeCrawler ! CrawlYoutubePage("http://www.youtube.com/watch?v=g8evyE9TuYk") // batman
//  youtubeCrawler ! CrawlYoutubePage("http://www.youtube.com/watch?v=-fcGzCbnTQE") // top anime openings, includes titan (few seconds)
//  youtubeCrawler ! CrawlYoutubePage("http://www.youtube.com/watch?v=fFK_YCS8ab0&list=WLFDF76FA8065675A9") // vocaloids
//  youtubeCrawler ! CrawlYoutubePage("http://www.youtube.com/watch?v=uAcD7H53220") // spiderman
//  youtubeCrawler ! CrawlYoutubePage("http://www.youtube.com/watch?v=GAkw_Wi4yIo") // tchaikovsky
//  youtubeCrawler ! CrawlYoutubePage("http://www.youtube.com/watch?v=hO6eQnH41ao") // project retouch, landscape

  youtubeCrawler ! CrawlYoutubePage("http://www.youtube.com/watch?v=IPlA2yUN_Bk") // bartender - HAS SUBTITLES
}
