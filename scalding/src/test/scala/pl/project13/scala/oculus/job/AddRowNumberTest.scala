package pl.project13.scala.oculus.job

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.twitter.scalding._
import pl.project13.scala.oculus.conversions.OculusRichPipe
import cascading.flow.Flow

class AddRowNumberTest extends FlatSpec with ShouldMatchers with TupleConversions
  with OculusRichPipe {

  "AddRowNumber" should "addRowNumbers" in {
    class AddRowNumberJob(args: Args) extends Job(args) {

      val inputFile = args("input")
      val outputFile = args("output")

      val ids = IterableSource(List(1,2,3), 'id)
        .read
        .debugWithFields

      TextLine(inputFile)
        .read
        .rename('line -> 'l)
        .debugWithFields
        .write(Tsv(outputFile))

      override def buildFlow(implicit mode : Mode): Flow[_] = {
        validateSources(mode)
        // Sources are good, now connect the flow:
        mode.newFlowConnector(config).connect(flowDef)
      }

    }

    JobTest(new AddRowNumberJob(_))
      .arg("input", "inFile")
      .arg("output", "outFile")
      .source(TextLine("inFile"), List(
        "0" -> "a",
        "1" -> "b",
        "2" -> "c")
      )
      .sink[(Int, String)](Tsv("outFile")) { out =>
        val data = out.toList

        data should contain (1, "a")
        data should contain (2, "b")
        data should contain (3, "c")
      }
      .run
      .finish
  }


}
