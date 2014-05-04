package pl.project13.scala.oculus

import java.io.{FileOutputStream, File}
import com.google.common.io.Resources
import pl.project13.scala.oculus.phash.PHash
import pl.project13.scala.oculus.distance.Distance
import com.google.common.base.Charsets

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

  val oneHash = PHash.analyzeImage(one)
  val twoHash = PHash.analyzeImage(two)

  println("===========================================")
  println("oneHash.dctHash = " + oneHash.dctHash)
  println("-------------------------------------------")
  println("oneHash.mhHash = " + oneHash.mhHash)
  println("===========================================")

  println("===========================================")
  println("twoHash.mhHash = " + twoHash.mhHash)
  println("-------------------------------------------")
  println("twoHash.dctHash = " + twoHash.dctHash)
  println("===========================================")

  println("-------------------------------------------")
  val dctDistance = Distance.hammingDistance(oneHash.dctHash.getBytes(Charsets.UTF_8), twoHash.dctHash.getBytes(Charsets.UTF_8))
  val mhDistance = Distance.hammingDistance(oneHash.mhHash.getBytes(Charsets.UTF_8), twoHash.mhHash.getBytes(Charsets.UTF_8))

  println("dctDistance = " + dctDistance)
  println("mhDistance = " + mhDistance)
  println("-------------------------------------------")


  println("name1 = " + name1)
  println("name2 = " + name2)

}