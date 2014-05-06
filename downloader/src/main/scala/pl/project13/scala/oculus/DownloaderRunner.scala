package pl.project13.scala.oculus

import actor._
import akka.actor.{AddressFromURIString, Props, ActorSystem}
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

  val remoteSystem = ActorSystem("remote-oculus-system", config)

  val fs = config.getString("oculus.hadoop.fs.defaultFS")
  val fps = config.getInt("oculus.ffmpeg.framesPerSecond")

  val hdfsUploader = system.actorOf(Props(new HDFSUploadActor(fs, fps)).withRouter(FromConfig), "hdfs-uploader")
  val youtubeDownloader = system.actorOf(Props(new YoutubeDownloadActor(hdfsUploader)).withRouter(FromConfig), "youtube-downloader")
  val youtubeCrawler = system.actorOf(Props(new YoutubeCrawlActor(youtubeDownloader)), "youtube-crawler")
  val conversionActor = system.actorOf(Props(new ConversionActor(hdfsUploader, "oculus/conv", 10)))

  // download specific videos

  youtubeDownloader ! DownloadFromYoutube("https://www.youtube.com/watch?v=YE7VzlLtp-4", crawl = false) // big buck bunny,                  creative commons
//  youtubeDownloader ! DownloadFromYoutube("https://www.youtube.com/watch?v=e98uKex3hSw", crawl = false) // big buck bunny, mirror,          creative commons

//  youtubeDownloader ! DownloadFromYoutube("https://www.youtube.com/watch?v=BHmGzBUIcAM", crawl = false) // big buck bunny, 60s-90s,         creative commons
//  youtubeDownloader ! DownloadFromYoutube("https://www.youtube.com/watch?v=bb0B316n1o8", crawl = false) // big buck bunny, 60s-90s, mirror, creative commons


//  youtubeDownloader ! DownloadFromYoutube("https://www.youtube.com/watch?v=TLkA0RELQ1g", crawl = false) // elephants dream, creative commons

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
//
//  youtubeDownloader ! DownloadFromYoutube("http://www.youtube.com/watch?v=IPlA2yUN_Bk", crawl = false) // bartender - HAS SUBTITLES
//  youtubeDownloader ! DownloadFromYoutube("http://www.youtube.com/watch?v=bcITDAXU5Vg", crawl = false) // TOP 50 anime
}
