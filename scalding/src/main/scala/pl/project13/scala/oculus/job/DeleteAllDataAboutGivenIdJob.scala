package pl.project13.scala.oculus.job

import com.twitter.scalding._
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import pl.project13.scala.oculus.conversions.{WriteDOT, OculusRichPipe}
import pl.project13.scala.oculus.source.hbase.{MetadataSource, HashesSource, HistogramsSource}
import org.apache.hadoop.hbase.client.{HTable, Delete}
import org.apache.hadoop.hbase.HBaseConfiguration

class DeleteAllDataAboutGivenIdJob(args: Args) extends Job(args)
  with WriteDOT
  with OculusRichPipe
  with HistogramsSource with HashesSource with MetadataSource
  with Histograms {

  implicit val hbaseConf = {
    val c = HBaseConfiguration.create()
    c.set("fs.default.name", "hdfs://10.240.57.179:9000")
    c.set("hbase.rootdir", "hdfs://10.240.57.179:9000/hbase")
    c.set("hbase.cluster.distributed", "true")
    c.set("hbase.zookeeper.quorum", "10.240.57.179")
    c
  }


  val inputId = args("id")

  val HashesHTable     = new HTable(hbaseConf, "hashes")
  val HistogramsHTable = new HTable(hbaseConf, "histograms")
  val MetadataHTable   = new HTable(hbaseConf, "metadata")

  implicit val mode = Read

  HashesTable
    .read
    .filter('id) { id: ImmutableBytesWritable => ibwToString(id) contains inputId }
    .map('mhHash -> ()) { key: ImmutableBytesWritable =>
      HashesHTable.delete(new Delete(key.get))
    }

  HistogramsTable
    .read
    .filter('id) { id: ImmutableBytesWritable => ibwToString(id) contains inputId }
    .map('lumHist -> ()) { key: ImmutableBytesWritable =>
      HashesHTable.delete(new Delete(key.get))
    }

  MetadataTable
    .read
    .filter('id) { id: ImmutableBytesWritable => ibwToString(id) contains inputId }
    .map('rowId -> ()) { key: ImmutableBytesWritable =>
      MetadataHTable.delete(new Delete(key.get))
    }

}

