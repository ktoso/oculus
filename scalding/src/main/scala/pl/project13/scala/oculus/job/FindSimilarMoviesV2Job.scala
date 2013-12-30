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

      .map('frame -> 'frameFrame) { sec: ImmutableBytesWritable => ibwToLong(sec) }
      .discard('frame)

      .rename('mhHash -> 'hashFrame)
      .limit(100)

  val otherHashes =
    HashesTable
      .read // todo enable filter not!!!
//      .filterNot('id) { id: ImmutableBytesWritable => ibwToString(id) contains inputId } // comment out, in order to see if "most similar is myself" works
      .map('id -> 'id) { id: ImmutableBytesWritable => ibwToString(id) }
      .rename('id -> 'idRef)

      .map('frame -> 'frameRef) { sec: ImmutableBytesWritable => ibwToLong(sec) }
      .discard('frame)

      .rename('mhHash -> 'hashRef)
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

  /**
   * write all distances we've calculated
   * TODO this can be probably skipped
   */
  val allDistancesSorted = distances
    .groupAll { _.sortBy('distance) }
    .write(Csv(outputDistances, writeHeader = true, fields = ('distance, 'idFrame, 'idRef, 'frameFrame, 'frameRef, 'hashFrame, 'hashRef)))


  /** find most similar reference frame for each input frame */
  val bestMatchingFrames = distances
    .groupBy('frameFrame) {
      _.sortWithTake(('distance, 'frameRef, 'idRef) -> 'topMatch, 1) {
        (t1: (Long, String, String), t2: (Long, String, String)) => t1._1 < t2._1
      }
    }
    .map('topMatch -> 'topMatch) { l: List[_] => l.head }
    .write(Csv(outputTopMostSimilarForFrame, writeHeader = true))


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

