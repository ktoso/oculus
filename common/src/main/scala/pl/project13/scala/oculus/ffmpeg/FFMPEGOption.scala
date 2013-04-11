package pl.project13.scala.oculus.ffmpeg

import java.io.File

abstract class FFMPEGOption {
  override def toString = str
  def str: String
}

case class InputFile(path: File) extends FFMPEGOption {
  def str = "-i " + path.getAbsolutePath
}

case class FramesPerSecond(count: Int) extends FFMPEGOption {
  def str = "-r " + count
}

case class OutputFormat(format: String) extends FFMPEGOption {
  def str = "-f " + format
}

case object OutputFormatPngImage2 extends OutputFormat("image2")

case class OutputFilePngNamePattern(name: String, numbersToPad: Int, ext: String) extends FFMPEGOption {
  def str = name + "%0d" + numbersToPad + "." + ext
}

case object OutputFilePngNamePattern extends OutputFilePngNamePattern("image", 9, "png")

