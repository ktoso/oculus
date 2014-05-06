package pl.project13.scala.oculus.job

import com.twitter.scalding._
import pl.project13.scala.oculus.IPs
import pl.project13.scala.scalding.hbase.{OculusStringConversions, MyHBaseSource}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import pl.project13.scala.oculus.distance.Distance
import org.apache.hadoop.io.BytesWritable
import pl.project13.scala.oculus.conversions.WriteDOT

class HistoCompareTwoMoviesJob(args: Args) extends Job(args)
  with WriteDOT
  with OculusStringConversions
  with Hashing {

  val refId = args("ref")
  val attackedId = args("attacked")

  val output = s"/oculus/compare-${refId}_AND_${attackedId}.out"
  

  // --- 

  val HistogramsTableRef = new MyHBaseSource(
    tableName = "histograms",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'key,
    familyNames = Array("youtube", "youtube", "hist",   "hist",     "hist",    "phash", "phash"),
    valueFields = Array('id,       'frame,    'redHist, 'greenHist, 'blueHist, 'mh,     'dct)
  )

  val HistogramsTableAttacked = new MyHBaseSource(
    tableName = "histograms",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'key,
    familyNames = Array("youtube", "youtube", "hist",   "hist",     "hist",    "phash", "phash"),
    valueFields = Array('id,       'frame,    'redHist, 'greenHist, 'blueHist, 'mh,     'dct)
  )

  val ref =
    HistogramsTableRef
      .read
      .map('id -> 'id) { id: ImmutableBytesWritable => ibwToString(id) }
      .map('frame -> 'frame) { f : ImmutableBytesWritable => ibwToLong(f) }
      .filter('id) { id: String => id contains refId }
      .rename('id -> 'idRef)
      .rename('frame -> 'frameRef)
      .rename('redHist -> 'redRef)
      .rename('greenHist -> 'greenRef)
      .rename('blueHist -> 'blueRef)
      .rename('mh -> 'hmRef)
      .rename('dct -> 'dctRef)

  val attacked = 
    HistogramsTableAttacked
      .read
      .map('id -> 'id) { id: ImmutableBytesWritable => ibwToString(id) }
      .map('frame -> 'frame) { f : ImmutableBytesWritable => ibwToLong(f) }
      .filter('id) { id: String => id contains attackedId }
      .rename('id -> 'id2)
      .rename('frame -> 'frame2)

  ref.joinWithSmaller('frame1 -> 'frame2, attacked)
    .map(('mhHash1, 'mhHash2) -> ('hash1, 'hash2, 'distance)) { x: (ImmutableBytesWritable, ImmutableBytesWritable) =>
      val (h1, h2) = x

      (
        h1.get.mkString(""),
        h2.get.mkString(""),
        Distance.hammingDistance(h1.get, h2.get)
      )
    }
    .groupAll {
      _.sortBy('distance)
    }
    .write(Csv(output, writeHeader = true, fields = ('distance, 'id1, 'id2, 'frame1, 'frame2, 'hash1, 'hash2)))

}

