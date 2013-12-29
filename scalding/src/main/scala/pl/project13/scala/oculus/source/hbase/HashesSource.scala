package pl.project13.scala.oculus.source.hbase

import pl.project13.scala.scalding.hbase.MyHBaseSource
import pl.project13.scala.oculus.IPs
import com.twitter.scalding.Job

trait HashesSource {
  this: Job =>

  val HashesTable = new MyHBaseSource(
    tableName = "hashes",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'mhHash,
    familyNames = Array("youtube", "youtube"),
    valueFields = Array('id,       'frame)
  )

}
