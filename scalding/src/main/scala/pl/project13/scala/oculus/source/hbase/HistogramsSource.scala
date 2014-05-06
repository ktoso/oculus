package pl.project13.scala.oculus.source.hbase

import com.twitter.scalding.Job
import pl.project13.scala.scalding.hbase.MyHBaseSource
import pl.project13.scala.oculus.IPs

trait HistogramsSource {
  this: Job =>

  val HistogramsTable = new MyHBaseSource(
    tableName = "histograms",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'key,
    familyNames = Array("youtube", "youtube", "hist",   "hist",     "hist",    "phash", "phash"),
    valueFields = Array('id,       'frame,    'redHist, 'greenHist, 'blueHist, 'mh,     'dct)
  )

}
