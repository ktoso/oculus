package pl.project13.scala.oculus.job

import com.twitter.scalding._
import pl.project13.scala.oculus.IPs
import pl.project13.scala.scalding.hbase.MyHBaseSource
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import pl.project13.scala.oculus.distance.Distance
import pl.project13.scala.oculus.conversions.{WriteDOT, OculusRichPipe}

class FindSimilarMoviesV2Job(args: Args) extends Job(args)
  with WriteDOT
  with OculusRichPipe
  with Histograms
  with Hashing {

  val TopNForFrame = 10

  /** seq file with images */
  val inputId = args("id")

  val outputDistances = "/oculus/similar-to-" + inputId + "-distances" + "-withself-25,50,50" + ".out"
  val outputRanking   = "/oculus/similar-to-" + inputId                + "-withself-25,50,50" + ".out"

  implicit val mode = Read

  val Hashes = new MyHBaseSource(
    tableName = "hashes",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'hash,
    familyNames = Array("youtube", "youtube"),
    valueFields = Array('id,       'second)
  )

  val Histograms = new MyHBaseSource(
    tableName = "histograms",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'lumHist,
    familyNames = Array("youtube", "youtube", "hist",   "hist",     "hist"),
    valueFields = Array('id,       'frame,    'redHist, 'greenHist, 'blueHist)
  )

  val inputHashes =
    Hashes
      .read
      .filter('id) { id: ImmutableBytesWritable => ibwToString(id) contains inputId }
      .map('id -> 'id) { id: ImmutableBytesWritable => ibwToString(id) }
      .rename('id -> 'idFrame)
      .rename('second -> 'secondFrame)
      .rename('hash -> 'hashFrame)
      .sample(25.0)
      .limit(2)

  val otherHashes =
    Hashes
      .read // todo enable filter not!!!
//      .filterNot('id) { id: ImmutableBytesWritable => ibwToString(id) contains inputId } // comment out, in order to see if "most similar is myself" works
      .map('id -> 'id) { id: ImmutableBytesWritable => ibwToString(id) }
      .rename('id -> 'idRef)
      .rename('second -> 'secondRef)
      .rename('hash -> 'hashRef)
      .sample(25.0)
      .limit(100)

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
    .write(Csv(outputDistances, writeHeader = true, fields = ('distance, 'idFrame, 'idRef, 'secondFrame, 'secondRef, 'hashFrame, 'hashRef)))

  // group and find most similar movies
  val totalDistances = distances
    .groupBy('secondFrame) {
      _.sortWithTake(('distance, 'idRef, 'secondRef) -> 'distanceForFrame, TopNForFrame) {
        (t1: (Long, Any, Any), t2: (Long, Any, Any)) => t1._1 < t2._1
      }
    }
    .debug
    .write(Csv(outputRanking, writeHeader = true))


}

