package pl.project13.scala.oculus.job

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.twitter.scalding._
import pl.project13.scala.oculus.conversions.OculusRichPipe
import com.twitter.algebird.{AveragedGroup, AveragedValue}

class OverTest extends FlatSpec with ShouldMatchers with TupleConversions with FieldConversions
  with OculusRichPipe {

  class OverJob(source: IterableSource[(String, Int)])(args: Args) extends Job(args) {
    val inputFile = args("input")
    val outputFile = args("output")

    val map1 = (x: Double) =>
      List(x -> AveragedValue(x))

    val red = (l: List[(Double, AveragedValue)], r: List[(Double, AveragedValue)]) => {
      val avg = AveragedGroup.plus(r.head._2, l.head._2)
      l.head.copy(_2 = avg) :: r.head.copy(_2 = avg) :: Nil
    }

    val map2 = (l: List[(Double, AveragedValue)]) => {
      val (age, avg) = l.unzip
      // all avg should have the same value anyway
      age -> avg.head.value
    }

    source
      .read
      .groupBy('cat) {
//        _.average('age -> 'avgAge)

//        _.mapPlusMap('age -> ('age, 'avgAge)) { (x : Double) =>
//          List(x) -> AveragedValue(1L, x)
//        } { t =>
//          println("t = " + t)
//          t._1 -> t._2.value
//        }

       _.mapReduceMap('age -> ('age -> 'avgAge))(map1)(red)(map2)
      }
//      .joinWithLarger('cat -> 'cat, source)
      .debugWithFields
      .write(Tsv(outputFile))

  }

  "Over" should "select the best match for each frame" in {
    val input = List(
      ("cat1", 10),
      ("cat1", 20),
      ("cat2", 45)
    )

    val expected = List(
      ("cat1", 10, 15),
      ("cat1", 20, 15),
      ("cat2", 45, 45)
    )

    JobTest(new OverJob(IterableSource(input, ('cat, 'age)))(_))
      .arg("input", "inFile")
      .arg("output", "outFile")
      .sink[(String, Int, Int)](Tsv("outFile")) { out =>
        val data = out.toList

        data should contain (expected.head)
        data should contain (expected.tail.head)
        data should contain (expected.tail.tail.head)
      }
      .run
      .finish
  }


}
