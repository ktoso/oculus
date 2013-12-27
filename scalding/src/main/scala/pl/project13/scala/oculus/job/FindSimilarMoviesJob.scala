package pl.project13.scala.oculus.job

import com.twitter.scalding._
import pl.project13.scala.oculus.IPs
import pl.project13.scala.scalding.hbase.MyHBaseSource
import org.apache.commons.io.FilenameUtils
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import pl.project13.scala.oculus.distance.Distance
import com.twitter.scalding.typed.Joiner
import pl.project13.scala.oculus.phash.PHash
import org.apache.hadoop.io.BytesWritable
import pl.project13.scala.oculus.conversions.{WriteDOT, OculusRichPipe}

class FindSimilarMoviesJob(args: Args) extends Job(args)
  with WriteDOT
  with OculusRichPipe
  with Histograms
  with Hashing {

  /** seq file with images */
  val inputId = args("id")

  val outputDistances = "/oculus/similar-to-" + inputId + "-distances.out"
  val outputRanking = "/oculus/similar-to-" + inputId + ".out"

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
      .limit(20)

  val otherHashes =
    Hashes
      .read
      .filterNot('id) { id: ImmutableBytesWritable => ibwToString(id) contains inputId } // comment out, in order to see if "most similar is myself" works
      .map('id -> 'id) { id: ImmutableBytesWritable => ibwToString(id) }
      .rename('id -> 'idRef)
      .rename('second -> 'secondRef)
      .rename('hash -> 'hashRef)

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
    .map(('hashFrame, 'hashRef) -> 'distance) { x: (ImmutableBytesWritable, ImmutableBytesWritable) =>
      val (hashFrame, hashRef) = x

      Distance.hammingDistance(hashFrame.get, hashRef.get)
    }
    .groupAll { _.sortBy('distance) }
    .write(Tsv(outputDistances, writeHeader = true, fields = ('distance, 'idFrame, 'idRef, 'secondFrame, 'secondRef, 'hashFrame, 'hashRef)))

  // group and find most similar movies
  val totalDistances = distances
    .groupBy('idRef) {
      _.sum('distance -> 'totalDistance) // sum of distances = total distance from each movie to this one
    }
    .map('totalDistance -> 'totalDistanceFormatted) { total: Double => f"$total%020.0f" } // simple formatting, instead of 23E7 notation
    .groupBy('idRef) {
      _.sortBy('totalDistance).reverse   // we want the lowest distance first
    }
    .write(Tsv(outputRanking, writeHeader = true, fields = ('totalDistanceFormatted, 'idRef)))


}

