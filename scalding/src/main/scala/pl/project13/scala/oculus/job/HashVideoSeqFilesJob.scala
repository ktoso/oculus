package pl.project13.scala.oculus.job

import com.twitter.scalding._
import pl.project13.scala.oculus.conversions.OculusTupleConversions
import scala.Array

class HashVideoSeqFilesJob(args: Args) extends Job(args)
  with OculusTupleConversions {

  val input = "/oculus/source/1-LfcKPtAwU.webm.seq"

  type SeqFileElement = (Int, Array[Byte])

  WritableSequenceFile(input, ('key, 'value))
    .read
    .mapTo(('key, 'value) -> 'length) { p: SeqFileElement => pHash(p._2) }
    .write(Tsv(output))

  def pHash(bytes: Array[Byte]): Int = {
    bytes.size
  }

}



