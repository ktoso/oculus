package pl.project13.scala.oculus.job

import com.twitter.scalding._
import pl.project13.scala.oculus.IPs
import pl.project13.scala.scalding.hbase.MyHBaseSource
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import pl.project13.scala.oculus.distance.Distance
import pl.project13.scala.oculus.conversions.{WriteDOT, OculusRichPipe}
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop._
import org.apache.hadoop.hbase.util.Bytes

class ExtractTextFromMovieJob(args: Args) extends Job(args)
  with WriteDOT
  with OculusRichPipe
  with Histograms
  with Hashing {

  /** seq file with images */
  val input = args("input")

  /** id to look for metadata */
  val id = args("id")

  val Metadata = new MyHBaseSource(
    tableName = "metadata",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'id,
    familyNames = Array("youtube", "youtube",  "text"),
    valueFields = Array('id,       'crawledAt, 'text)

  )

  val meta = MetadataSchema.MetadataTable
    .query2
    .withKey(id)
    .withFamilies(_.youtube, _.textFamily)
    .singleOption() match {
      case Some(row) => row.column(_.text).get
      case None      => throw new RuntimeException(s"Unable to find metadata row for $id!")
    }


}


import java.lang.String
import org.joda.time.{DateMidnight, DateTime}
import com.gravity.hbase.schema._
import scala.collection._

import org.apache.hadoop.hbase.HBaseConfiguration

trait HBaseConfig {

  implicit val hbaseConf = {
    val c = HBaseConfiguration.create()
    c.set("fs.default.name", "hdfs://10.240.57.179:9000")
    c.set("hbase.rootdir", "hdfs://10.240.57.179:9000/hbase")
    c.set("hbase.cluster.distributed", "true")
    c.set("hbase.zookeeper.quorum", "10.240.57.179")
    c
  }

}

object MetadataSchema extends Schema with HBaseConfig {

  import com.gravity.hbase.schema

  class MetadataTable extends HbaseTable[MetadataTable, String, PropsTableRow]("metadata", new NoOpCache[MetadataTable, String, PropsTableRow](), classOf[String])(hbaseConf, schema.StringConverter) {

    def rowBuilder(result: DeserializedResult) = new PropsTableRow(this, result)

    val youtube = family[String, String, Any]("youtube")

    val json = column(youtube, "json", classOf[String])
    val crawledAt = column(youtube, "crawledAt", classOf[DateTime])

    val textFamily = family[String, String, Any]("text")
    val text = column(youtube, "text", classOf[String])
  }

  class PropsTableRow(table: MetadataTable, result: DeserializedResult) extends HRow[MetadataTable, String](result, table)

  val MetadataTable = table(new MetadataTable)

}