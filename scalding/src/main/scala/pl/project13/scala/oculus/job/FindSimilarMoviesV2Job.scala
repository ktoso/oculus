package pl.project13.scala.oculus.job

import com.twitter.scalding._
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import pl.project13.scala.oculus.distance.Distance
import pl.project13.scala.oculus.conversions.{WriteDOT, OculusRichPipe}
import pl.project13.scala.oculus.source.hbase.{HashesSource, HistogramsSource}

class FindSimilarMoviesV2Job(args: Args) extends Job(args)
  with WriteDOT
  with OculusRichPipe
  with HistogramsSource with HashesSource
  with Histograms with Hashing {

  val TopKForFrame = 10

  /** seq file with images */
  val inputId = args("id")

  val outputDistances              = "/oculus/similar-to-" + inputId + "-distances" + "-withself-5_5" + ".out"
  val outputRanking                = "/oculus/similar-to-" + inputId + "-ranking"   + "-withself-5_5" + ".out"
  val outputTopMostSimilarForFrame = "/oculus/similar-to-" + inputId + "-top"       + "-withself-5_5" + ".out"

  implicit val mode = Read

  val inputHashes =
    HashesTable
      .read
      .filter('id) { id: ImmutableBytesWritable => ibwToString(id) contains inputId }
      .map('id -> 'id) { id: ImmutableBytesWritable => ibwToString(id) }
      .rename('id -> 'idFrame)
      .mapTo('frame -> 'frameFrame) { sec: ImmutableBytesWritable => ibwToLong(sec) }
      .rename('hash -> 'hashFrame)
      .limit(100)

  val otherHashes =
    HashesTable
      .read // todo enable filter not!!!
//      .filterNot('id) { id: ImmutableBytesWritable => ibwToString(id) contains inputId } // comment out, in order to see if "most similar is myself" works
      .map('id -> 'id) { id: ImmutableBytesWritable => ibwToString(id) }
      .rename('id -> 'idRef)
      .mapTo('frame -> 'frameRef) { sec: ImmutableBytesWritable => ibwToLong(sec) }
      .rename('hash -> 'hashRef)
      .sample(5.0)
      .limit(200)
      .debug

//  val inputHistograms =
//    Histograms
//      .read
//      .filter('id) { id: ImmutableBytesWritable => ibwToString(id) contains inputId }
//      .rename('id -> 'histId)
//
//  val otherHistograms =
//    Histograms
//      .read
//      .filterNot('id) { id: ImmutableBytesWritable => ibwToString(id) contains inputId }
//      .rename('id -> 'histId)

//  inputHashes.joinWithLarger('hashId -> 'histId, inputHistograms)

  val distances = otherHashes.crossWithTiny(inputHashes)
//    .sample(50.0)
    .map(('hashFrame, 'hashRef) -> 'distance) { x: (ImmutableBytesWritable, ImmutableBytesWritable) =>
      val (hashFrame, hashRef) = x

      Distance.hammingDistance(hashFrame.get, hashRef.get)
    }
    .groupAll { _.sortBy('distance) }
    .write(Csv(outputDistances, writeHeader = true, fields = ('distance, 'idFrame, 'idRef, 'frameFrame, 'frameRef, 'hashFrame, 'hashRef)))

  // group and find most similar movies
  val totalDistances = distances
    .map('hashRef   -> 'hashRef) { h: ImmutableBytesWritable => ibwToString(h) }
    .map('hashFrame -> 'hashFrame) { h: ImmutableBytesWritable => ibwToString(h) }
    .groupBy('frameFrame) {
      _.sortWithTake(('distance, 'idRef, 'frameRef) -> 'distanceForFrame, TopKForFrame) {
        (t1: (Int, Any, Any), t2: (Int, Any, Any)) => t1._1 < t2._1
      }
    }
    .write(Csv(outputRanking, writeHeader = true, fields = ('distance,   'idFrame, 'frameFrame,   'idRef, 'frameRef,   'distanceForFrame)))

  val mostSimilarMovie = totalDistances
    .groupBy('idRef) {
      _.size('countOfSimilarMovie)
       .sortWithTake('countOfSimilarMovie -> 'c, 2) { (c1: Int, c2: Int) => c1 < c2 }
    }
    .write(Csv(outputTopMostSimilarForFrame, writeHeader = true, fields = ('idRef, 'c)))

}

