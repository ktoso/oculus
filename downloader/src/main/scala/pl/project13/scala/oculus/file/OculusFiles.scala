package pl.project13.scala.oculus.file

import java.io.File

trait LocalFile {
  def file: File
}

object LocalFile {
  def unapply(s: LocalFile): Some[File] = {
    Some(s.file)
  }
}

case class DownloadedVideoFile(dir: File, name: String, ext: String) extends LocalFile {
  val fullName = name + "." + ext

  override val file = new File(dir, fullName)
}
