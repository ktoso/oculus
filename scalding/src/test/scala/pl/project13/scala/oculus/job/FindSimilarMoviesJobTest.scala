package pl.project13.scala.oculus.job

import org.scalatest.{FlatSpec, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import com.twitter.scalding._

class FindSimilarMoviesJobTest extends FlatSpec with ShouldMatchers
  with LowPriorityConversions {

  behavior of "FindSimilarMovies"

  it should "find a similar thing" in {

    JobTest(new FindSimilarMovies(_))
      .registerFile("inputFile")
      .registerFile("ranked")
      .arg("input", "inputFile")
      .arg("output", "ranked")
      .sink[String](Tsv("ranked")) { case ranked =>
        println("ranked = " + ranked.mkString(" | "))
        ranked.toList should equal (List(List(0.0, 4.0, 6.0)))
      }
      .runHadoop
      .finish
  }
}
