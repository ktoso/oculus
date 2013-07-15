package pl.project13.scala.oculus.job

import com.twitter.scalding._
import pl.project13.scala.oculus.conversions.OculusTupleConversions
import scala.Array

class VideoToPicturesJob(args: Args) extends Job(args)
  with OculusTupleConversions {

  val input = args("input")
  val output = args("output")

  type SeqFileElement = (Int, Array[Byte])

//  TextLine(input)
////    .flatMap('line -> 'image) { video: String => convert(asLocalFile(inputFile)) }
////    .groupAll
//    .write(SequenceFile(output))
//

  Tsv("").read.write(Tsv(""))

  IterableSource(List(1, 2, 30, 42), 'num)
    .map('num -> 'lessThanTen) { i: String => i.split(" ").map(_.toInt) }


  WritableSequenceFile(input, ('key, 'value))
    .read
    .limit(100)
    .mapTo(('key, 'value) -> 'length) { p: SeqFileElement => pHash(p._2) }
    .write(Tsv(output))

  def pHash(bytes: Array[Byte]): Int = {
    bytes.size
  }

}



