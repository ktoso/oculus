package pl.project13.scala.oculus.job

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.twitter.scalding._

class WordCountJobTest extends FlatSpec with ShouldMatchers with TupleConversions {

  "WordCountJob" should "count words" in {
    JobTest(new WordCountJob(_))
      .arg("input", "inFile")
      .arg("output", "outFile")
      .source(TextLine("inFile"), List("0" -> "kapi kapi pi pu po"))
      .sink[(String, Int)](Tsv("outFile")) { out =>
        out.toList should contain ("kapi" -> 2)
      }
      .run
      .finish
  }


}
