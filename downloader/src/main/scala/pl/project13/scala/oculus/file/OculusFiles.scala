package pl.project13.scala.oculus.file

import java.io.File

trait LocalFile {
  def path: String
}

object LocalFile {
  def unapply(s: LocalFile): Some[String] = {
    Some(s.path)
  }
}

case class DownloadedVideoFile(dir: File, name: String, ext: String) extends LocalFile {
  val fullName = name + "." + ext

  override val path = new File(dir, fullName)
}
