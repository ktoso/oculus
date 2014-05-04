package pl.project13.scala.oculus

import pl.project13.common.utils.Histogram
import java.io.{FileOutputStream, File}
import com.google.common.io.Resources
import javax.imageio.ImageIO
import java.awt.Color

object HashDistancesApp extends App {

//  val name1 = "image919.png"
//  val name1 = "bunny-standing.png"

//  val name1 = "bunny-butterfly.png"
//  val name2 = "bunny-butterfly-5plus-light.png"

  val name1 = "Big_Buck_Bunny_mirror.png"
  val name2 = "Big_Buck_Bunny_normal.png"

  val R = "R"
  val G = "G"
  val B = "B"

  val one = new File("/tmp", name1)
  val two = new File("/tmp", name2)
  Resources.copy(Resources.getResource(name1), new FileOutputStream(one))
  Resources.copy(Resources.getResource(name2), new FileOutputStream(two))

//  val oneHash = PHash.analyzeImage(one)
//  val twoHash = PHash.analyzeImage(two)
//
//  println("-------------------------------------------")
//  println("oneHash.dctHash = " + oneHash.dctHash)
//  println("oneHash.mhHash = " + oneHash.mhHash)
//  println("-------------------------------------------")

  val h1 = Histogram.getHisrogram(name1)
  val h2 = Histogram.getHisrogram(name2)

  val h1Dominates = dominatingColor(h1)
  val h2Dominates = dominatingColor(h2)
  println("h1Dominates = " + h1Dominates)
  println("h2Dominates = " + h2Dominates)


  println("====")
  val d1 = dominant(one)
  val d2 = dominant(two)
  println("d1 = " + d1)
  println("d2 = " + d2)

  println("=========")
  println("one = " + one)
  println("two = " + two)
//  println("distance = " + distance)

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

  def dominatingColor(h: Histogram) = {
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

    println("red part   = " + rc)
    println("green part = " + gc)
    println("blue part  = " + bc)

    (rc :: gc :: bc :: Nil).sortBy(- _._2).head
  }

  case class DominatingColors(cols: List[(String, Int)], dominating: String, key: String)
}