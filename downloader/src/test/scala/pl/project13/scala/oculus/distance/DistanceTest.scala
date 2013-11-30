package pl.project13.scala.oculus.distance

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.google.common.base.Charsets

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

  it should "calculate distance between two known hashes" in {
    // given
    val b1   = "f8a1d2b1bc083c92da4e82786416d8b725141aa4d48d254c6127228c8e8ecc91ea3562144d075dc6db4897a5a46a61701486d1b18b0bf20e7249a6e74b13205b0f966a19044a2540".getBytes(Charsets.UTF_8)
    val near = "f8b589454f97132b5cff7170900c0003fcaea7e5000000000163e2f6fec00000000c6000d5c8bfc04005dc3e047c8ef3938be7693dc2c426366c5e4b1c95a5f7caaa3176780f4b55".getBytes(Charsets.UTF_8)
    val far  = "0085925b2b1ecd4a04255719ba6ee2d8d8edc9fd2db258dda2039401eead9667a4893c76d329494d33697163ac6ce972c94f100df12263cb12cec4397870e8dba4ae74dd030f87cf".getBytes(Charsets.UTF_8)

    // when
    val distNear = hammingDistance(b1, near)
    val distFar = hammingDistance(b1, far)

    // then
    info("dist far  = " + distNear)
    info("dist near = " + distFar)

    distNear should be < distFar
  }

}
