package pl.project13.scala.oculus.phash

import java.io.File

import scala.sys.process._

object PHash {

  val MhPrefix = "mh_hash: "
  val DctPrefix = "dct_hash: "
  val Zero = "0"
  val ZeroByte = Zero.getBytes.head

  object MH {
    def decode(b: String): Array[Int] = {
      val noLeadingZeros = b.dropWhile(_ == ZeroByte)
      noLeadingZeros.sliding(2, 2).map(a => Integer.parseInt(a, 16)).toArray
    }
  }
  object DCT {
    def decode(s: String): String = {
      s
    }
  }

  case class PHashResult(mhHash: String, dctHash: String)

  def analyzeImage(f: File): PHashResult = {
    val checkfile = Process("file " + f.getAbsoluteFile).!!
    println("file is: " + checkfile)

    val cmnd = List("image_hashes", f.getAbsoluteFile) mkString " "

    println("Exec: " + cmnd)
    val output = Process(cmnd).!!

    parse(output)
  }

  private[phash] def parse(s: String): PHashResult = {
    val lines = s.split("\n")
    val mh = lines.find(_ contains MhPrefix) map { _.replace(MhPrefix, "") } getOrElse { throw new RuntimeException(s"Unable to find mh hash, in: [$s]") }
    val dct = lines.find(_ contains DctPrefix) map { _.replace(DctPrefix, "") } getOrElse { throw new RuntimeException(s"Unable to find dct hash, in: [$s]") }

    println("mh = " + mh)
    println("dct = " + dct)

    PHashResult(
      mhHash = mh.split(" ").map(a => padLeft(a.toInt.toHexString, 2, Zero)).mkString,
      dctHash = dct.replaceAll(" ", "")
    )
  }

  private def zeroPadded(s: Array[String], width: Int, b: String): Array[String] = {
    val l = s.length
    if (l >= width){
      s
    } else {
      val a = new Array[String](width)
      var i = 0
      val until = width - l
      while (i < until) {
        a(i) = b
        i += 1
      }
      System.arraycopy(s, 0, a, i, l)

      a
    }
  }

  private def padLeft(s: String, width: Int, ch: String) = {
    val l = s.length
    val left = width - l
    (ch * left) + s
  }

}
