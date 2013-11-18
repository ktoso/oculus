package pl.project13.scala.oculus.job

import com.twitter.scalding._
import cascading.tuple._
import pl.project13.scala.oculus.IPs
import com.twitter.maple.hbase.{HBaseTap, HBaseScheme}
import org.apache.hadoop.mapred.{JobConf, RecordReader, OutputCollector}
import cascading.scheme.Scheme
import cascading.tap.{SinkMode, Tap}

class HashVideoSeqFilesJob(args: Args) extends Job(args) {

  val inputFile = args("input")
  val _outputFile = args("output")

  val TableName = Array("hashes")
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

  class MyHBaseSource(
    tableName: String,
    quorumNames: String = "localhost",
    keyFields: Fields,
    familyNames: Array[String],
    valueFields: Array[Fields]) extends Source {

    override val hdfsScheme = new HBaseScheme(keyFields, familyNames, valueFields)
      .asInstanceOf[Scheme[JobConf, RecordReader[_, _], OutputCollector[_, _], _, _]]

    override def createTap(readOrWrite: AccessMode)(implicit mode: Mode): Tap[_, _, _] = {
      val hBaseScheme = hdfsScheme match {
        case hbase: HBaseScheme => hbase
        case _ => throw new ClassCastException("Failed casting from Scheme to HBaseScheme")
      }
      mode match {
        case hdfsMode @ Hdfs(_, _) => readOrWrite match {
          case Read => {
            new HBaseTap(quorumNames, tableName, hBaseScheme, SinkMode.KEEP)
          }
          case Write => {
            new HBaseTap(quorumNames, tableName, hBaseScheme, SinkMode.UPDATE)
          }
        }
        case _ => super.createTap(readOrWrite)(mode)
      }
    }
  }

  val WriteHashesColumn = new MyHBaseSource(
    tableName = "hashes",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'hash,
    familyNames = Array("props"),
    valueFields = Array('hash)
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

