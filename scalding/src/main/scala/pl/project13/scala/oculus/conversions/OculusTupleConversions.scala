package pl.project13.scala.oculus.conversions

import cascading.tuple.TupleEntry
import cascading.tuple.TupleEntryIterator
import cascading.tuple.{Tuple => CTuple}
import cascading.tuple.Tuples
import com.twitter.scalding.TupleGetter


trait OculusTupleConversions {

  implicit object ByteArrayGetter extends TupleGetter[Array[Byte]] {
    override def get(tup : CTuple, i : Int) = tup.getString(i).getBytes
  }

}
