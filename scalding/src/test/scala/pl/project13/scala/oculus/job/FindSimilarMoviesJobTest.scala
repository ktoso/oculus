package pl.project13.scala.oculus.job

import org.scalatest.{FlatSpec, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import com.twitter.scalding._

class FindSimilarMoviesJobTest extends FlatSpec with ShouldMatchers
  with LowPriorityConversions {

  behavior of "FindSimilarMovies"

  it should "find a similar thing" in {

    JobTest(new WordCountJob(_))
      .registerFile("inputFile")
      .registerFile("ranked")
      .arg("input", "inputFile")
      .arg("output", "ranked")
      .sink[(Int, Int, Double)](Tsv("ranked")) { r =>
        r foreach println

        r.head should equal (1, 1, 1.0)
      }
      .runHadoop
      .finish
  }
}
