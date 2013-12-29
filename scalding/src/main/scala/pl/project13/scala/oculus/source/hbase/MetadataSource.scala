package pl.project13.scala.oculus.source.hbase

import com.twitter.scalding.Job
import pl.project13.scala.scalding.hbase.MyHBaseSource
import pl.project13.scala.oculus.IPs

trait MetadataSource {
  this: Job =>

  val MetadataTable = new MyHBaseSource(
    tableName = "metadata",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'rowId,
    familyNames = Array("youtube", "youtube",  "text"),
    valueFields = Array('id,       'crawledAt, 'text)

  )

}
