package pl.project13.scala.oculus.hbase

import pl.project13.scala.oculus.hbase.tables.{HistogramsSchema, HashesSchema, MetadataSchema}
import com.gravity.hbase.schema.ColumnFamily
import org.apache.hadoop.hbase.HColumnDescriptor
import org.apache.hadoop.hbase.util.Bytes

object HBaseInit {

  import pl.project13.scala.rainbow._

  def init() {
    println("Prepare HBase with the following tables:".green)

    val tables =
      MetadataSchema.MetadataTable ::
        HashesSchema.HashesTable ::
        HistogramsSchema.HistogramsTable ::
        Nil

    tables foreach { table =>
      println {
        var create = "create '" + table.tableName + "', "
        create += (table.families map familyDef).mkString(",")
        create
      }
    }
  }


  def familyDef(family: ColumnFamily[_, _, _, _, _]) = {
      val compression = if (family.compressed) ", COMPRESSION=>'lzo'" else ""
      val ttl = if (family.ttlInSeconds < HColumnDescriptor.DEFAULT_TTL) ", TTL=>'" + family.ttlInSeconds + "'" else ""
      "{NAME => '" + Bytes.toString(family.familyBytes) + "', VERSIONS => " + family.versions + compression + ttl + "}"
    }
}
