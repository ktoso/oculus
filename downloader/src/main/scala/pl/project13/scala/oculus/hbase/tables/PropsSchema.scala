package pl.project13.scala.oculus.hbase.tables

import java.lang.String
import org.joda.time.{DateMidnight, DateTime}
import com.gravity.hbase.schema._
import scala.collection._
import pl.project13.scala.oculus.hbase.HBaseConfig

object PropsSchema extends Schema with HBaseConfig {

  import com.gravity.hbase.schema

  class MetadataTable extends HbaseTable[MetadataTable, String, PropsTableRow]("metadata", new NoOpCache[MetadataTable, String, PropsTableRow](), classOf[String])(hbaseConf, schema.StringConverter) {

    def rowBuilder(result: DeserializedResult) = new PropsTableRow(this, result)

    val youtube = family[String, String, Any]("youtube")

    val json = column(youtube, "title", classOf[String])
    val crawledAt = column(youtube, "crawledAt", classOf[DateTime])

  }

  class PropsTableRow(table: MetadataTable, result: DeserializedResult) extends HRow[MetadataTable, String](result, table)

  val MetadataTable = table(new MetadataTable)
}