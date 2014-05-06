package pl.project13.scala.oculus.job

import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import pl.project13.scala.oculus.phash.PHash
import java.io.File
import com.google.common.io.Files
import org.apache.hadoop.io.BytesWritable
import pl.project13.scala.oculus.conversions.OculusTupleConversions
import pl.project13.scala.scalding.hbase.OculusStringConversions
import pl.project13.common.utils.Histogram
import javax.imageio.ImageIO
import java.awt.Color

trait Histograms extends OculusTupleConversions with OculusStringConversions {

//  type SeqFileElement = (Int, BytesWritable)

  def mkHistogram(seqFileEl: (Int, BytesWritable)): (DominatingColors, Histogram) = {
    val (idx, bytes) = seqFileEl
    val bs = bytes.getBytes

    val result = onTmpFile(bs) { f =>
      dominant(f) -> Histogram.getHisrogram(f.getAbsolutePath)
    }

    result
  }

  private def onTmpFile[T](bytes: Array[Byte])(block: File => T): T = {
    val f = File.createTempFile("oculus-histograms", ".png")
    try {
      Files.write(bytes, f)
      block(f)
    } finally {
      f.delete()
    }
  }

  private val R = "R"
  private val G = "G"
  private val B = "B"

  def dominant(f: File) = {
    val img = ImageIO.read(f)

    val cols = for {
      x <- 0 until img.getWidth
      y <- 0 until img.getHeight
      c = new Color(img.getRGB(x, y))
      r = c.getRed
      g = c.getGreen
      b = c.getBlue
    } yield if (r >= g && r >= b) R
       else if (g >= r && g >= b) G
       else if (b >= r && b >= g) B
       else "_"

    val rc = cols.count(_ == R)
    val gc = cols.count(_ == G)
    val bc = cols.count(_ == B)
    val all = cols.size

    def ratio(in: Int): Int = (in.toDouble / all * 100).toInt
    def mkKey(cols: List[(String, Int)]): String = {
      val ordered = cols.sortBy(- _._2)
      (ordered.map { case (k, v) => s"$k${ratio(v)}" }).mkString("-")
    }

    val ordered = List(R -> rc, G -> gc, B -> bc).sortBy(-_._2)
    DominatingColors(ordered, ordered.head._1, mkKey(ordered))
  }


  def dominatingColor(h: Histogram): (String, Int) = {
     val zipped = (h.getRedHistogram zip h.getGreenHistogram zip h.getBlueHistogram) map {
       case ((r,g), b) => (r, g, b)
     }

     val dominates = zipped map { case (r,g,b) =>
       if (r >= g && r >= b) R
       else if (g >= r && g >= b) G
       else if (b >= r && b >= g) B
       else "_"
     }

     val rc = R -> dominates.count(_ == R)
     val gc = G -> dominates.count(_ == G)
     val bc = B -> dominates.count(_ == B)

     (rc :: gc :: bc :: Nil).sortBy(- _._2).head
   }

   case class DominatingColors(cols: List[(String, Int)], dominating: String, key: String)

}
