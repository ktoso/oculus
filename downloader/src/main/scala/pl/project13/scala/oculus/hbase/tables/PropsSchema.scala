package pl.project13.scala.oculus.hbase.tables

import java.lang.String
import org.joda.time.{DateMidnight, DateTime}
import com.gravity.hbase.schema._
import scala.collection._
import pl.project13.scala.oculus.hbase.HBaseConfig

object PropsSchema extends Schema with HBaseConfig {

  import com.gravity.hbase.schema

  class PropsTable extends HbaseTable[PropsTable, String, PropsTableRow]("props", new NoOpCache[PropsTable, String, PropsTableRow](), classOf[String])(hbaseConf, schema.StringConverter) {

    def rowBuilder(result: DeserializedResult) = new PropsTableRow(this, result)

    val meta = family[String, String, Any]("meta")

    val title = column(meta, "title", classOf[String])
    val lastCrawled = column(meta, "lastCrawled", classOf[DateTime])

    val content = family[String, String, Any]("text", compressed = true)
    val article = column(content, "article", classOf[String])
    val attributes = column(content, "attrs", classOf[Map[String, String]])

    val searchMetrics = family[String, DateMidnight, Long]("searchesByDay")
    val it = table(new PropsTable)

  }


  class PropsTableRow(table: PropsTable, result: DeserializedResult) extends HRow[PropsTable, String](result, table)

  val PropsTable = table(new PropsTable)
}