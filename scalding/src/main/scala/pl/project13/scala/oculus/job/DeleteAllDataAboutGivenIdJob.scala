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

  val inputId = args("id")

  implicit val mode = Read

  HashesTable
    .read
    .filter('id) { id: ImmutableBytesWritable => ibwToString(id) contains inputId }
//    .map('mhHash -> ()) { key: ImmutableBytesWritable =>
//      HashesHTable.delete(new Delete(key.get))
//    }
    .write(Csv("/trash/del-1", fields = 'mhHash))

  HistogramsTable
    .read
    .filter('id) { id: ImmutableBytesWritable => ibwToString(id) contains inputId }
//    .map('lumHist -> ()) { key: ImmutableBytesWritable =>
//      HashesHTable.delete(new Delete(key.get))
//    }
    .write(Csv("/trash/del-2", fields = 'lumHist))

  MetadataTable
    .read
    .filter('id) { id: ImmutableBytesWritable => ibwToString(id) contains inputId }
//    .map('rowId -> ()) { key: ImmutableBytesWritable =>
//      MetadataHTable.delete(new Delete(key.get))
//    }
    .write(Csv("/trash/del-3", fields = 'rowId))

}

