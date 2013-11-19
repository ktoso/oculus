package pl.project13.scala.oculus.phash

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import pl.project13.scala.oculus.phash.PHash.PHashResult

class PHashTest extends FlatSpec with ShouldMatchers {

  behavior of "PHash"

  it should "parse output" in {
    // given
    val consoleOutput =
      """|mh_hash: 66 248 221 150 127 29 1 183 4 203 178 146 205 127 64 224 143 117 72 166 211 100 115 233 203 98 39 30 121 185 216 50 110 72 141 172 120 148 102 1
         |dct_hash: 15489049597769751684""".stripMargin

    // when
    val PHashResult(mhHash, dctHash) = PHash.parse(consoleOutput)

    info("mhHash = " + mhHash.mkString)
    info("dctHash = " + dctHash.mkString)

    // then
    mhHash.length should be >= 80
    dctHash.length should be >= 80
  }
}
