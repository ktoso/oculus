package pl.project13.scala.oculus

import java.awt._
import javax.imageio.ImageIO
import javax.swing.JPanel
import pl.project13.common.utils.Histogram
import java.io.File
import javax.swing.JFrame

object HistogramApp extends App {

  val file = args(0)

  private val d = new Dimension(800, 800)
  val frame = new HistogramFrame(d, new File(file))
  frame.setVisible(true)

}

class HistogramFrame(d: Dimension, pic: File) extends JFrame("Histograms: " + pic.getName) {
  setLayout(new GridLayout(1, 4))
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  setSize(d)
  setVisible(true)

  val d2 = getSize
  d2.height = d.height / 2
  d2.width = d.width / 2
  val picPanel = new PicturePanel(d2, pic)

  val hist = Histogram.getHisrogram(pic.getAbsolutePath)

  val redPanel = new HistogramPanel(d2, hist, "red")
  val greenPanel = new HistogramPanel(d2, hist, "green")
  val bluePanel = new HistogramPanel(d2, hist, "blue")
//  val grayPanel = new HistogramPanel(d2, hist, "gray")
//  val lumPanel = new HistogramPanel(d2, hist, "lum")

  add(picPanel)

  add(redPanel)
  add(greenPanel)
  add(bluePanel)
//  add(grayPanel)
//  add(lumPanel)
}


class PicturePanel(dimFrame: Dimension, picture: File) extends JPanel {

  override def paintComponent(g: Graphics) {
    super.paintComponent(g)
    val inputPicture = ImageIO.read(picture)
    g.drawImage(inputPicture, 0, 0, dimFrame.height, dimFrame.width, this)
  }

}

class HistogramPanel(dimFrame: Dimension, hist: Histogram, color: String) extends JPanel {

  var samples: Array[Int] = color match {
    case "red" => hist.getRedHistogram
    case "green" => hist.getGreenHistogram
    case "blue" => hist.getBlueHistogram
    case "gray" => hist.getGrayHistogram
    case "lum" => hist.getLuminanceHistogram
  }

  override def paintComponent(g: Graphics) {
    super.paintComponent(g)

    val posX = 0
    val posY = 0
    color match {
      case "red" => g.setColor(Color.RED)
      case "green" => g.setColor(Color.GREEN)
      case "blue" => g.setColor(Color.BLUE)
      case "gray" => g.setColor(Color.BLACK)
      case "lum" => g.setColor(Color.LIGHT_GRAY)
    }

    val mx = 18316

    val b = 142
    for (i <- 0 until 255) {
      val h = samples(i) * 255 / mx
      g.drawLine(i, b + 255 - h, i, b + 255)
    }

    g.setColor(Color.WHITE)
    color match {
       case "red" => g.drawString("red", posX, posY)
       case "green" => g.drawString("red", posX, posY)
       case "blue" => g.drawString("blue", posX, posY)
       case "gray" => g.drawString("gray", posX, posY)
       case "lum" => g.drawString("Luminance", posX, posY)
     }

  }

}

