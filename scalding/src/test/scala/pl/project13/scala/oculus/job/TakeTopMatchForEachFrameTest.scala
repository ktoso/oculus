package pl.project13.scala.oculus.job

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.twitter.scalding._

class TakeTopMatchForEachFrameTest extends FlatSpec with ShouldMatchers with TupleConversions {

  "TakeTopMatchForEachFrame" should "select the best match for each frame" in {
    val input = List(
      TakeTopMatchData("id1", 5,  "ff1", "fr111"),
      TakeTopMatchData("id3", 29, "ff1", "fr3"  ),
      TakeTopMatchData("id2", 10, "ff1", "fr42" ),
      TakeTopMatchData("id1", 20, "ff2", "fr34" ),
      TakeTopMatchData("id1", 12, "ff2", "fr222"),
      TakeTopMatchData("id4", 32, "ff2", "fr44" ),
      TakeTopMatchData("id2", 5,  "ff3", "fr26" )
    )

    JobTest(new TakeTopMatchForEachFrameJob(IterableSource(input))(_))
      .arg("input", "inFile")
      .arg("output", "outFile")
      .sink[(String, (Int, String, String))](Tsv("outFile")) { out =>
        val data = out.toList

        data should contain ("ff1", (5,  "fr111", "id1"))
        data should contain ("ff2", (12, "fr222", "id1"))
        data should contain ("ff3", (5,  "fr26",  "id2"))
      }
      .run
      .finish
  }


}
