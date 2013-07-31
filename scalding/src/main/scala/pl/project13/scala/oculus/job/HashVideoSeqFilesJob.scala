package pl.project13.scala.oculus.job

import com.twitter.scalding._
import pl.project13.scala.oculus.conversions.OculusTupleConversions
import scala.Array
import parallelai.spyglass.hbase.HBaseSource
import cascading.tuple.Fields
import scala.compat.Platform

class HashVideoSeqFilesJob(args: Args) extends Job(args)
  with OculusTupleConversions {

  val input = "/oculus/source/1-LfcKPtAwU.webm.seq"

  val TableName = "frames"
  val TableSchema = 'youtube :: Nil

  type SeqFileElement = (Int, Array[Byte])

  WritableSequenceFile(input, ('key, 'value))
    .read
    .mapTo(('key, 'value) -> 'phash) { p: SeqFileElement => pHash(p._2) }
    .write(
      new HBaseSource(
        TableName,
        "quorum_name:2181",
        'phash,
        TableSchema.tail.map((x: Symbol) => "youtube"),
        TableSchema.tail.map((x: Symbol) => new Fields(x.name)),
        timestamp = Platform.currentTime
      )
    )

  // todo implement native call
  def pHash(bytes: Array[Byte]): String = {
    bytes.size.toString
  }

}



