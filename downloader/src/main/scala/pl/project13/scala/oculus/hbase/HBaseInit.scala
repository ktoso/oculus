package pl.project13.scala.oculus.hbase

import pl.project13.scala.oculus.hbase.tables.MetadataSchema
import com.gravity.hbase.schema.ColumnFamily
import org.apache.hadoop.hbase.HColumnDescriptor
import org.apache.hadoop.hbase.util.Bytes
import pl.project13.scala.oculus.hbase.tables.MetadataSchema.MetadataTable

object HBaseInit {
  
  def init() {
    println("Prepare HBase with the following tables:")
    println(createScript(MetadataSchema.MetadataTable))
  }

  def createScript[T](table: MetadataTable) = {
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
