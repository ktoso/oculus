package pl.project13.scala.oculus

import file.LocalFile

package object actor {

  case class CrawlYoutubePage(url: String)

  case class DownloadFromYoutube(url: String)

  case class UploadToHDFS(file: LocalFile)

}
