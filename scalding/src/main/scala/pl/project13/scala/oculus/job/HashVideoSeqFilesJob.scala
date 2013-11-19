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

class HashVideoSeqFilesJob(args: Args) extends Job(args) {

  val _inputFile = args("input")
  val _outputFile = args("output")

  val TableName = Array("metadata")
  val TableSchema = Array("youtube")

  type SeqFileElement = (Int, String)

//  tableName: String,
//  quorumNames: String,
//  keyFields: cascading.tuple.Fields,
//  familyNames: Array[String],
//  valueFields: Array[cascading.tuple.Fields],
//  timestamp: Long,
//  sourceMode: parallelai.spyglass.hbase.HBaseConstants.SourceMode,
//  startKey: String,
//  stopKey: String,
//  keyList: List[String],
//  versions: Int,
//  useSalt: Boolean,
//  prefixList: String


  // hash -> youtube id
  val WriteHashesColumn = new MyHBaseSource(
    tableName = "hashes",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'hash,
    familyNames = Array("youtube"),
    valueFields = Array('id)
  )

//  val WriteHashesColumn = new MyHBaseSource(
//    tableName = "hashes",
//    quorumNames = IPs.HadoopMasterWithPort,
//    keyFields = 'hash,
//    familyNames = Array("props"),
//    valueFields = Array('hash),
//    timestamp = Platform.currentTime,
//    sourceMode = SourceMode.SCAN_ALL,
//    versions = 1,
//    useSalt = false
//  )

  val youtubeId = FilenameUtils.getBaseName(_inputFile)

  WritableSequenceFile(_inputFile, ('key, 'value))
    .read
    .mapTo(('key, 'value) -> 'hash) { p: SeqFileElement => pHash(p._2) }
    .project('hash)
    .write(WriteHashesColumn)

  // todo implement native call
  def pHash(bytes: String): String = {
    bytes.length.toString
  }

}

