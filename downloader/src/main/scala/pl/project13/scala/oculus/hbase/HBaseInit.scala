package pl.project13.scala.oculus.hbase

import pl.project13.scala.oculus.hbase.tables.{HashesSchema, MetadataSchema}
import com.gravity.hbase.schema.{HbaseTable, ColumnFamily}
import org.apache.hadoop.hbase.HColumnDescriptor
import org.apache.hadoop.hbase.util.Bytes
import pl.project13.scala.oculus.hbase.tables.MetadataSchema.MetadataTable

object HBaseInit {

  import pl.project13.scala.rainbow._

  def init() {
    println("Prepare HBase with the following tables:".green)
    println(createScript(MetadataSchema.MetadataTable).bold)
    println(createScript(HashesSchema.HashesTable).bold)
  }

  def createScript[T](table: HbaseTable) = {
      var create = "create '" + table.tableName + "', "
      create += (for (family <- table.families) yield {
        familyDef(family)
      }).mkString(",")

      create
    }

  def familyDef(family: ColumnFamily[_, _, _, _, _]) = {
      val compression = if (family.compressed) ", COMPRESSION=>'lzo'" else ""
      val ttl = if (family.ttlInSeconds < HColumnDescriptor.DEFAULT_TTL) ", TTL=>'" + family.ttlInSeconds + "'" else ""
      "{NAME => '" + Bytes.toString(family.familyBytes) + "', VERSIONS => " + family.versions + compression + ttl + "}"
    }
}
