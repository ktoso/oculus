package pl.project13.scala.oculus.conversions

import com.twitter.scalding._
import cascading.tuple.Fields
import cascading.pipe.{Each, Pipe}
import cascading.operation.Debug
import cascading.flow.FlowDef
import com.twitter.scalding.Csv

trait OculusRichPipe {

  /**
   * Scalding does not have filterNot...
   */
  implicit class EnrichedScaldinRichPipe(pipe: Pipe) {
    def filterNot[A](f : Fields)(fn : (A) => Boolean)
                 (implicit conv : TupleConverter[A]) : Pipe = {
        conv.assertArityMatches(f)
        val negated = (a: A) => !fn(a)
        new Each(pipe, f, new FilterFunction(negated, conv))
      }

    def debugWithFields = {
      val d = new Debug(true)
      new Each(pipe, d)
    }

    def trapOculusErrorTuples(implicit flowDef : FlowDef, mode : Mode) = {
      flowDef.addTrap(pipe, Csv("/oculus/error-tuples", writeHeader = true).createTap(Write)(mode))
      pipe
    }
  }

}
