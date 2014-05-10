package pl.project13.scala.oculus.job

import com.twitter.scalding._
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import pl.project13.scala.oculus.distance.Distance
import pl.project13.scala.oculus.conversions.{WriteDOT, OculusRichPipe}
import pl.project13.scala.oculus.source.hbase.{HashesSource, HistogramsSource}
import pl.project13.scala.scalding.hbase.MyHBaseSource
import pl.project13.scala.oculus.IPs

class FindSimilarMoviesV2Job(args: Args) extends Job(args)
  with WriteDOT
  with OculusRichPipe
  with HistogramsSource with HashesSource
  with Histograms with Hashing {

  /** seq file with images */
  val inputId = args("id")

  val outputDistances              = "/oculus/similar-to-" + inputId + "-distances" + ".out"
  val outputRanking                = "/oculus/similar-to-" + inputId + "-ranking"   + ".out"
  val outputTopMostSimilarForFrame = "/oculus/similar-to-" + inputId + "-top"       + ".out"
  val outputTopMatch               = "/oculus/similar-to-" + inputId + "-top-match" + ".out"

  implicit val mode = Read

  val HistogramsTableRef = new MyHBaseSource(
    tableName = "histograms",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'key,
    familyNames = Array("youtube", "youtube", "hist",   "hist",     "hist",    "phash", "phash"),
    valueFields = Array('id,       'frame,    'redHist, 'greenHist, 'blueHist, 'mh,     'dct)
  )
  
  val referenceTable =
    HistogramsTableRef
      .read
      .map('id -> 'id) { id: ImmutableBytesWritable => ibwToString(id) }
      .filter('id) { id: String => id contains inputId }
      .map('frame -> 'frame) { f: ImmutableBytesWritable => ibwToLong(f) }
      .map('key -> 'dominantColRef) { k: ImmutableBytesWritable => ibwToString(k).take(2).mkString("") }
      .rename('key -> 'keyRef)
      .rename('id -> 'idRef)
      .rename('frame -> 'frameRef)
      .rename('redHist -> 'redRef)
      .rename('greenHist -> 'greenRef)
      .rename('blueHist -> 'blueRef)
      .rename('mh -> 'mhRef)
      .rename('dct -> 'dctRef)

  val analysedMovie =
    HistogramsTableRef
      .read
      .map('id -> 'id) { id: ImmutableBytesWritable => ibwToString(id) }
      .filterNot('id) { id: String => id contains inputId } // comment out, in order to see if "most similar is myself" works
      .map('frame -> 'frame) { f : ImmutableBytesWritable => ibwToLong(f) }
      .map('key -> 'dominantColAtt) { k: ImmutableBytesWritable => ibwToString(k).take(2).mkString("") }
      .rename('key -> 'keyAtt)
      .rename('id -> 'idAtt)
      .rename('frame -> 'frameAtt)
      .rename('redHist -> 'redAtt)
      .rename('greenHist -> 'greenAtt)
      .rename('blueHist -> 'blueAtt)
      .rename('mh -> 'mhAtt)
      .rename('dct -> 'dctAtt)
      .sample(75.0)
      .debug

  val distances = referenceTable.joinWithSmaller('dominantColRef -> 'dominantColAtt, analysedMovie)
    .map(('mhRef, 'mhAtt) -> 'distance) { x: (ImmutableBytesWritable, ImmutableBytesWritable) =>
      val (mhRef, mhAtt) = x

      Distance.hammingDistance(mhRef.get, mhAtt.get)
    }

  /** find most similar reference frame for each input frame */
  val bestMatchingFrames = distances
    .addTrap(Csv("/oculus/error-tuples", writeHeader = true))
    .groupBy('frameAtt) {
      _.sortBy('distance).head('idRef)
//      _.sortWithTake(('distance, 'frameRef, 'idRef) -> 'topMatch, 1) {
//        (t1: (Int, String, String), t2: (Int, String, String)) =>
//          if (t1 == null || t2 == null) false
//          else try {
//            t1._1 < t2._1
//          } catch {
//            case n: NullPointerException =>
//              System.err.println(s"Error: Got ${n.getClass.getName}, for t1:${t1}, t2:${t2}")
//              false
//            case ex: Throwable =>
//              System.err.println(s"Error: Got , for t1:${t1}, t2:${t2}")
//              false
//          }
//      }
    }
//    .map('topMatch -> ('distance, 'frameRef, 'idRef)) { l: List[(Int, String, String)] => l.head }
//    .debugWithFields("after all, distances calculated and grouped")
    .write(Csv(outputTopMostSimilarForFrame, writeHeader = true, fields = ('frameAtt, 'distance, 'idRef)))

  val bestMatchingMovie = bestMatchingFrames
    .groupBy('idRef) { _.size('movieMatchedNTimes) }
    .groupAll { _.sortBy('movieMatchedNTimes) }
    .write(Csv(outputTopMatch, writeHeader = true, fields = 'idRef))


//  val totalDistances = distances
//    .map('hashRef   -> 'hashRef) { h: ImmutableBytesWritable => ibwToString(h) }
//    .map('hashFrame -> 'hashFrame) { h: ImmutableBytesWritable => ibwToString(h) }
//    .groupBy('frameFrame) {
//      _.sortedTake[Long]('distance -> 'distanceForFrame, TopKForFrame)
//    }
//    .write(Csv(outputRanking, writeHeader = true, fields = ('distance,   'idFrame, 'frameFrame,   'idRef, 'frameRef,   'distanceForFrame)))

//  override def next = Some(
//    new FindTopMatchFromFrameMatchesJob(
//      Args(
//        "--id"    :: inputId ::
//        "--input" :: outputRanking ::
//          Nil
//      )
//    )
//  )


}

