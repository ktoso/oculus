package pl.project13.scala.scalding.hbase

import cascading.tuple.Fields
import com.twitter.scalding._
import com.twitter.maple.hbase.{HBaseTap, HBaseScheme}
import cascading.scheme.Scheme
import org.apache.hadoop.mapred.{OutputCollector, RecordReader, JobConf}
import cascading.tap.{SinkMode, Tap}
import com.twitter.scalding.Hdfs

class MyHBaseSource(
    tableName: String,
    quorumNames: String,
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
          case Read =>
            new HBaseTap(quorumNames, tableName, hBaseScheme, SinkMode.KEEP)
          case Write =>
            new HBaseTap(quorumNames, tableName, hBaseScheme, SinkMode.UPDATE)
        }
        case _ => super.createTap(readOrWrite)(mode)
      }
    }
  }
