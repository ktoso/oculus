package pl.project13.scala.oculus.job

import com.twitter.scalding._
import cascading.tuple._
import pl.project13.scala.oculus.IPs
import com.twitter.maple.hbase.{HBaseTap, HBaseScheme}
import org.apache.hadoop.mapred.{JobConf, RecordReader, OutputCollector}
import cascading.scheme.Scheme
import cascading.tap.{SinkMode, Tap}
import pl.project13.scala.scalding.hbase.MyHBaseSource
import org.apache.commons.io.FilenameUtils
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import com.google.common.base.Charsets

class HashVideoSeqFilesJob(args: Args) extends Job(args) {

  val _inputFile = args("input")

  val TableName = Array("metadata")
  val TableSchema = Array("youtube")

  type SeqFileElement = (Int, String)

  implicit val mode = Read

  // key: hash -> (family)youtube:id
  val WriteHashesColumn = new MyHBaseSource(
    tableName = "hashes",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'hash,
    familyNames = Array("youtube"),
    valueFields = Array('id)
  )

  val youtubeId = FilenameUtils.getBaseName(_inputFile)

  WritableSequenceFile(_inputFile, ('key, 'value))
    .read
    .mapTo(('key, 'value) -> 'hash) { p: SeqFileElement => pHash(p._2) }
    .map('hash -> 'id) { h: ImmutableBytesWritable => youtubeId }
    .write(WriteHashesColumn)

  // todo implement native call
  def pHash(bytes: String): ImmutableBytesWritable = {
    val hash = bytes.length.toString // todo call phash here!!!!!!

    new ImmutableBytesWritable(hash.getBytes(Charsets.UTF_8))
  }

}

