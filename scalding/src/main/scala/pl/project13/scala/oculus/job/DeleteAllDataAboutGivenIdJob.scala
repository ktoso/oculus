package pl.project13.scala.oculus.job

import com.twitter.scalding._
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import pl.project13.scala.oculus.distance.Distance
import pl.project13.scala.oculus.conversions.{WriteDOT, OculusRichPipe}
import pl.project13.scala.oculus.source.hbase.{HashesSource, HistogramsSource}
import org.apache.hadoop.hbase.client.Delete
import org.apache.hadoop.hbase.ipc.HBaseClient
import com.gravity.hbase.schema._
import pl.project13.scala.oculus.hbase.HBaseConfig
import java.lang.String
import scala.Predef.String
import com.gravity.hbase.schema.DeserializedResult

class DeleteAllDataAboutGivenIdJob(args: Args) extends Job(args)
  with WriteDOT
  with OculusRichPipe
  with HistogramsSource with HashesSource
  with Histograms with Hashing {

  /** seq file with images */
  val inputId = args("id")

  implicit val mode = Read

  val inputHashes =
    HashesTable
      .read
      .filter('id) { id: ImmutableBytesWritable => ibwToString(id) contains inputId }
      .map('mhHash -> 0) { hash: ImmutableBytesWritable =>
        HashesSchema.HashesTable
          .delete(ibwToString(hash))
          .execute()
      }


}


object HashesSchema extends Schema with HBaseConfig {

  import com.gravity.hbase.schema

  class HashesTable extends HbaseTable[HashesTable, String, PropsTableRow]("hashes", new NoOpCache[HashesTable, String, PropsTableRow](), classOf[String])(hbaseConf, schema.StringConverter) {

    def rowBuilder(result: DeserializedResult) = new PropsTableRow(this, result)

    val youtube = family[String, String, Any]("youtube")

    /** can be used to look up movie in 'metadata' by key */
    val id    = column(youtube, "id", classOf[String])

    /** frame number, from which this hash was calculated (image####.png, here #### is our `frame`) */
    val frame = column(youtube, "frame", classOf[Int])

  }

  class PropsTableRow(table: HashesTable, result: DeserializedResult) extends HRow[HashesTable, String](result, table)

  val HashesTable = table(new HashesTable)

}