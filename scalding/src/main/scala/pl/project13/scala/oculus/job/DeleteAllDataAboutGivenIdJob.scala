package pl.project13.scala.oculus.job

import com.twitter.scalding._
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import pl.project13.scala.oculus.distance.Distance
import pl.project13.scala.oculus.conversions.{WriteDOT, OculusRichPipe}
import pl.project13.scala.oculus.source.hbase.{MetadataSource, HashesSource, HistogramsSource}
import org.apache.hadoop.hbase.client.{HTable, Delete}
import org.apache.hadoop.hbase.ipc.HBaseClient
import com.gravity.hbase.schema._
import pl.project13.scala.oculus.hbase.HBaseConfig
import java.lang.String
import scala.Predef.String
import com.gravity.hbase.schema.DeserializedResult

class DeleteAllDataAboutGivenIdJob(args: Args) extends Job(args)
  with WriteDOT
  with OculusRichPipe
  with HistogramsSource with HashesSource with MetadataSource
  with HBaseConfig
  with Histograms {

  val inputId = args("id")

  val HashesHTable     = new HTable(hbaseConf, "hashes")
  val HistogramsHTable = new HTable(hbaseConf, "histograms")
  val MetadataHTable   = new HTable(hbaseConf, "metadata")

  implicit val mode = Read

  HashesTable
    .read
    .filter('id) { id: ImmutableBytesWritable => ibwToString(id) contains inputId }
    .map('mhHash -> 'lol) { key: ImmutableBytesWritable =>
      HashesHTable.delete(new Delete(key.get))
    }

  HistogramsTable
    .read
    .filter('id) { id: ImmutableBytesWritable => ibwToString(id) contains inputId }
    .map('lumHist -> 'lol) { key: ImmutableBytesWritable =>
      HashesHTable.delete(new Delete(key.get))
    }

  MetadataTable
    .read
    .filter('id) { id: ImmutableBytesWritable => ibwToString(id) contains inputId }
    .map('rowId -> 'lol) { key: ImmutableBytesWritable =>
      MetadataHTable.delete(new Delete(key.get))
    }

}

