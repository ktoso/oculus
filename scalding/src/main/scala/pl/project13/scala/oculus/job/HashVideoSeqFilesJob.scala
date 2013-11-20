package pl.project13.scala.oculus.job

import com.twitter.scalding._
import cascading.tuple._
import pl.project13.scala.oculus.IPs
import com.twitter.maple.hbase.{HBaseTap, HBaseScheme}
import org.apache.hadoop.mapred.{JobConf, RecordReader, OutputCollector}
import cascading.scheme.Scheme
import cascading.tap.{SinkMode, Tap}
import pl.project13.scala.scalding.hbase.{OculusStringConversions, MyHBaseSource}
import org.apache.commons.io.FilenameUtils
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import com.google.common.base.Charsets
import pl.project13.scala.oculus.phash.PHash
import java.io.File
import com.google.common.io.Files
import org.apache.hadoop.io.BytesWritable

class HashVideoSeqFilesJob(args: Args) extends Job(args)
  with OculusStringConversions {

  val _inputFile = args("input")

  val TableName = Array("metadata")
  val TableSchema = Array("youtube")

  type SeqFileElement = (Int, BytesWritable)

  implicit val mode = Read

  // key: hash -> (family)youtube:id
  val WriteHashesColumn = new MyHBaseSource(
    tableName = "hashes",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'mhHash,
    familyNames = Array("youtube"),
    valueFields = Array('id)
  )

  val youtubeId = FilenameUtils.getBaseName(_inputFile)

  WritableSequenceFile(_inputFile, ('key, 'value))
    .read
    .mapTo(('key, 'value) -> 'mhHash) { p: SeqFileElement => mhHash(p) }
    .map('mhHash -> 'id) { h: ImmutableBytesWritable => youtubeId.asImmutableBytesWriteable } // because hbase Sink will cast to it, we need ALL fields as these
    .write(WriteHashesColumn)

  // todo do the same with dct hash!!!!!
  def mhHash(seqFileEl: SeqFileElement): ImmutableBytesWritable = {
    val (idx, bytes) = seqFileEl
    val bs = bytes.getBytes

    println(s"processing element [$idx] of sequence file [$youtubeId]. [size: ${bs.size}]")

    val result = onTmpFile(bs) { f =>
      PHash.analyzeImage(f)
    }

    result.mhHash.asImmutableBytesWriteable
  }
  
  def onTmpFile[T](bytes: Array[Byte])(block: File => T): T = {
    val f = File.createTempFile("oculus-hashing", ".png")
    try {
      Files.write(bytes, f)
      block(f)
    } finally {
//      f.delete() // todo uncomment!!!!
    }
  }

}

