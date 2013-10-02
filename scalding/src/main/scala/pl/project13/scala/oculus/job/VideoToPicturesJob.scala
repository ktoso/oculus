package pl.project13.scala.oculus.job

import com.twitter.scalding._
import pl.project13.scala.oculus.conversions.OculusTupleConversions
import scala.Array

class VideoToPicturesJob(args: Args) extends Job(args)
  with OculusTupleConversions {

  val input = args("input")
  val output = args("output")

  type SeqFileElement = (Int, Array[Byte])

  TextLine(input)
//    .flatMap('line -> 'image) { video: String => convert(asLocalFile(inputFile)) }
//    .groupAll
    .write(SequenceFile(output))

  def pHash(bytes: Array[Byte]): Int = {
    bytes.size
  }

}



