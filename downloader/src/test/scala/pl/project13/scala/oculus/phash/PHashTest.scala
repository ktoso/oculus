package pl.project13.scala.oculus.phash

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import pl.project13.scala.oculus.phash.PHash.PHashResult

class PHashTest extends FlatSpec with ShouldMatchers {

  behavior of "PHash"

  var mhHash: String = _
  var dctHash: String = _

  it should "parse output" in {
    // given
    val consoleOutput =
      """|mh_hash: 66 248 221 150 127 29 1 183 4 203 178 146 205 127 64 224 143 117 72 166 211 100 115 233 203 98 39 30 121 185 216 50 110 72 141 172 120 148 102 1
         |dct_hash: 15489049597769751684""".stripMargin

    // when
    val PHashResult(mhHash, dctHash) = PHash.parse(consoleOutput)

    this.mhHash = mhHash
    this.dctHash = dctHash

    info("mhHash  = " + mhHash.mkString)
    info("dctHash = " + dctHash.mkString)

    // no assert here
  }

  it should "can decode MH encoded hashes (from hbase keys)" in {
    // when
    val got = PHash.MH.decode(mhHash)
    info("decoded mh = " + got.mkString(" "))

    // then
    val expected = Array(66, 248, 221, 150, 127, 29, 1, 183, 4, 203, 178, 146, 205, 127, 64, 224, 143, 117, 72, 166, 211, 100, 115, 233, 203, 98, 39, 30, 121, 185, 216, 50, 110, 72, 141, 172, 120, 148, 102, 1)
    got.length should equal (expected.length)
    got should equal (expected)
  }

  // in case I decide to encode it somehow
  it should "can decode DCT encoded hashes (from hbase keys)" in {
    // when
    val got = PHash.DCT.decode(dctHash)
    info("decoded dct = " + got.mkString(" "))

    // then
    val expected = "15489049597769751684"
    got should equal (expected)
  }
}
