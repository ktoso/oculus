package pl.project13.scala.oculus.distance

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class DistanceTest extends FlatSpec with ShouldMatchers {

  behavior of "Hamming distance"

  import Distance._

  it should "calculate distance between byte arrays" in {
    // given
    val b1 = Array(5.toByte, 7.toByte) // 0b00000101, 0b00000111
    val b2 = Array(6.toByte, 8.toByte) // 0b00000110, 0b00001000

    // when
    val dist = hammingDistance(b1, b2)

    // then
    dist should equal(6)
  }

}
