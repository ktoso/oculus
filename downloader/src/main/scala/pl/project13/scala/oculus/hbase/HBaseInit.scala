package pl.project13.scala.oculus.hbase

import pl.project13.scala.oculus.hbase.tables.MetadataSchema

object HBaseInit {
  
  def init() {
    println(
      s"""
        |Prepare HBase with the following tables:
        |
        |${MetadataSchema.MetadataTable.createScript()}
        |
      """.stripMargin)
  }
}
