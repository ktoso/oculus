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
      .map('frame -> 'frame) { f: ImmutableBytesWritable => ibwToLong(f) }
      .map('key -> 'dominantColRef) { k: ImmutableBytesWritable => ibwToString(k).take(2).mkString("") }
      .filter('id) { id: String => id contains refId }
      .rename('key -> 'keyRef)
      .rename('id -> 'idRef)
      .rename('frame -> 'frameRef)
      .rename('redHist -> 'redRef)
      .rename('greenHist -> 'greenRef)
      .rename('blueHist -> 'blueRef)
      .rename('mh -> 'mhRef)
      .rename('dct -> 'dctRef)

  val attacked = 
    HistogramsTableAttacked
      .read
      .map('id -> 'id) { id: ImmutableBytesWritable => ibwToString(id) }
      .map('frame -> 'frame) { f : ImmutableBytesWritable => ibwToLong(f) }
      .map('key -> 'dominantColAtt) { k: ImmutableBytesWritable => ibwToString(k).take(2).mkString("") }
      .filter('id) { id: String => id contains attackedId }
      .rename('key -> 'keyAtt)
      .rename('id -> 'idAtt)
      .rename('frame -> 'frameAtt)
      .rename('redHist -> 'redAtt)
      .rename('greenHist -> 'greenAtt)
      .rename('blueHist -> 'blueAtt)
      .rename('mh -> 'mhAtt)
      .rename('dct -> 'dctAtt)

  type IBW = ImmutableBytesWritable

  ref.joinWithSmaller('dominantColRef -> 'dominantColAtt, attacked)
    .map(('mhRef, 'mhAtt, 'dctRef, 'dctAtt, 'keyRef, 'keyAtt) -> ('dctDist, 'mhDist, 'histDist)) {
      x: (IBW, IBW, IBW, IBW, IBW, IBW) =>

        val (mhRef, mhAtt, dctRef, dctAtt, keyRef, keyAtt) = x

        val dctDist = Distance.hammingDistance(dctRef.get, dctAtt.get)
        val mhDist = Distance.hammingDistance(mhRef.get, mhAtt.get)
        val histoDist = Distance.hammingDistance(ibwToString(keyRef).split(":").head.getBytes, ibwToString(keyAtt).split(":").head.getBytes)

      (
        dctDist,
        mhDist,
        histoDist
      )
    }
    .groupAll {
      _.sortBy('frameAtt, 'dctDist, 'histDist, 'mhDist)
    }
    .write(Csv(output, writeHeader = true, fields = ('frameAtt, 'frameRef, 'dctDist, 'histDist, 'mhDist, 'idRef, 'idAtt)))

}

