package pl.project13.scala.scalding.hbase

import org.scalatest.{FlatSpec, FunSuite}
import org.scalatest.matchers.ShouldMatchers

class OculusStringConversionsTest extends FlatSpec with ShouldMatchers
  with OculusStringConversions {


  it should "convert Long into both ways" in {
    // given
    val long = 12L

    // when
    val ibw = longToIbw(long)
    val got = ibwToLong(ibw)

    // then
    got should equal (long)
  }

}
