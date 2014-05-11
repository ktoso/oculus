package pl.project13.scala.oculus.job

import com.twitter.scalding._
import pl.project13.scala.oculus.IPs
import pl.project13.scala.scalding.hbase.{OculusStringConversions, MyHBaseSource}
import pl.project13.scala.oculus.conversions.WriteDOT
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import pl.project13.scala.oculus.distance.Distance

class ScenePositioningTwoMoviesJob(args: Args) extends Job(args)
  with WriteDOT
  with OculusStringConversions
  with Hashing {

  val movieId = args("movie")
  val sceneId = args("scene")

  val output = s"/oculus/scene-${sceneId}_in_${movieId}.out"
  

  // --- 

  val HistogramsTable = new MyHBaseSource(
    tableName = "histograms",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'key,
    familyNames = Array("youtube", "youtube", "hist",   "hist",     "hist",    "phash", "phash"),
    valueFields = Array('id,       'frame,    'redHist, 'greenHist, 'blueHist, 'mh,     'dct)
  )

  val movie =
    HistogramsTable
      .read
      .map('id -> 'id) { id: ImmutableBytesWritable => ibwToString(id) }
      .map('frame -> 'frame) { f: ImmutableBytesWritable => ibwToLong(f) }
      .map('key -> 'dominantColRef) { k: ImmutableBytesWritable => ibwToString(k).take(2).mkString("") }
      .filter('id) { id: String => id contains movieId }
      .rename('key -> 'keyRef)
      .rename('id -> 'idRef)
      .rename('frame -> 'frameRef)
      .rename('redHist -> 'redRef)
      .rename('greenHist -> 'greenRef)
      .rename('blueHist -> 'blueRef)
      .rename('mh -> 'mhRef)
      .rename('dct -> 'dctRef)

  val scene =
    HistogramsTable
      .read
      .map('id -> 'id) { id: ImmutableBytesWritable => ibwToString(id) }
      .map('frame -> 'frame) { f : ImmutableBytesWritable => ibwToLong(f) }
      .map('key -> 'dominantColAtt) { k: ImmutableBytesWritable => ibwToString(k).take(2).mkString("") }
      .filter('id) { id: String => id contains sceneId }
      .rename('key -> 'keyAtt)
      .rename('id -> 'idAtt)
      .rename('frame -> 'frameAtt)
      .rename('redHist -> 'redAtt)
      .rename('greenHist -> 'greenAtt)
      .rename('blueHist -> 'blueAtt)
      .rename('mh -> 'mhAtt)
      .rename('dct -> 'dctAtt)

  type IBW = ImmutableBytesWritable

  movie.joinWithSmaller('dominantColRef -> 'dominantColAtt, scene)
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
    .groupBy('frameAtt) {
      _.sortBy('mhDist).head('idRef)
    }
    .groupAll {
      _.sortBy('frameAtt)
    }
    .write(Csv(output, writeHeader = true, fields = ('frameAtt, 'frameRef, 'dctDist, 'histDist, 'mhDist, 'idRef, 'idAtt)))

}

