package pl.project13.scala.oculus

import file.LocalFile

package object actor {

  case class CrawlYoutubePage(url: String)
  case class DownloadFromYoutube(url: String, crawl: Boolean)

  case class RequestUploadToHDFS(file: LocalFile)
  case class UploadFileToHDFS(file: LocalFile)
  case class UploadAsSequenceFileToHDFS(file: LocalFile)

}
