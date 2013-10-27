package pl.project13.scala.oculus.hbase

import pl.project13.scala.oculus.hbase.tables.MetadataSchema

object HBaseInit {
  
  def init() {
    println("Prepare HBase with the following tables:")
    println(MetadataSchema.MetadataTable.createScript())
  }
}
