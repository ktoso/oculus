package pl.project13.scala.oculus.job

import com.twitter.scalding._
import parallelai.spyglass.hbase.HBaseSource
import scala.compat.Platform
import cascading.tuple._
import pl.project13.scala.oculus.IPs
import parallelai.spyglass.hbase.HBaseConstants.SourceMode

class HashVideoSeqFilesJob(args: Args) extends Job(args) {

  val inputFile = args("input")
  val _outputFile = args("output")

  val TableName = Array("hashes")
  val TableSchema = Array("youtube")

  type SeqFileElement = (Int, String)

  val WriteHashesColumn = new HBaseSource(
    "hashes",
    IPs.HadoopMasterWithPort,
    'hash,
    Array("props"),
    Array('hash),
    timestamp = Platform.currentTime
  )


  WritableSequenceFile(inputFile, ('key, 'value))
    .read
    .mapTo(('key, 'value) -> 'phash) { p: SeqFileElement => pHash(p._2) }
    .project('phash)
    .write(WriteHashesColumn)

  // todo implement native call
  def pHash(bytes: String): String = {
    bytes.length.toString
  }

}

