package pl.project13.scala.oculus

import java.awt._
import javax.imageio.ImageIO
import javax.swing.JPanel
import pl.project13.common.utils.Histogram
import java.io.File
import javax.swing.JFrame
import pl.project13.scala.oculus.phash.PHash

  object HistogramRatioAndDistancesApp extends App {

  val one = new File("docs/img/bunny-butterfly-5plus-light.png")
  val two = new File("docs/img/bunny-butterfly.png")

  val oneHash = PHash.analyzeImage(one)
  val twoHash = PHash.analyzeImage(two)

  println("oneHash = " + oneHash)
  println("twoHash = " + twoHash)

  println("one = " + one)
  println("two = " + two)
//  println("distance = " + distance)

}