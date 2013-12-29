package pl.project13.scala.oculus.conversions

import com.twitter.scalding.{FilterFunction, TupleConverter}
import cascading.tuple.Fields
import cascading.pipe.{Each, Pipe}

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
  }

}
