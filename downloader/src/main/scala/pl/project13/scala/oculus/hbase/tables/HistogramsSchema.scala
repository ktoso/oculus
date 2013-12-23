package pl.project13.scala.oculus.hbase.tables

import java.lang.String
import org.joda.time.{DateMidnight, DateTime}
import com.gravity.hbase.schema._
import scala.collection._
import pl.project13.scala.oculus.hbase.HBaseConfig

/**
 * Key = lumi histogram
 */
object HistogramsSchema extends Schema with HBaseConfig {

  import com.gravity.hbase.schema

  class HistogramsTable extends HbaseTable[HistogramsTable, String, PropsTableRow]("histograms", new NoOpCache[HistogramsTable, String, PropsTableRow](), classOf[String])(hbaseConf, schema.StringConverter) {

    def rowBuilder(result: DeserializedResult) = new PropsTableRow(this, result)

    val youtube = family[String, String, Any]("youtube")
    val hist    = family[String, String, Any]("hist")

    /** can be used to look up movie in 'metadata' by key */
    val id    = column(youtube, "id", classOf[String])

    /** frame number, from which this hash was calculated (image####.png, here #### is our `frame`) */
    val frame = column(youtube, "frame", classOf[Int])

    /** red histogram */
    val red = column(hist, "red", classOf[String])

    /** green histogram */
    val green = column(hist, "green", classOf[String])

    /** blue histogram */
    val blue = column(hist, "blue", classOf[String])

  }

  class PropsTableRow(table: HistogramsTable, result: DeserializedResult) extends HRow[HistogramsTable, String](result, table)

  val HistogramsTable = table(new HistogramsTable)

}