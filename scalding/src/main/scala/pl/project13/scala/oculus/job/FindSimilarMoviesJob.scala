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
import pl.project13.scala.oculus.conversions.OculusRichPipe

class FindSimilarMoviesJob(args: Args) extends Job(args)
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

  var takeTopK = 500

  val inputHashes =
    Hashes
      .read
      .filter('id) { id: ImmutableBytesWritable => ibwToString(id) contains inputId }
      .map('id -> 'id) { id: ImmutableBytesWritable => ibwToString(id) }
      .rename('id -> 'idFrame)
      .rename('second -> 'secondFrame)
      .rename('hash -> 'hashFrame)
      .limit(10)

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

//  inputHashes.joinWithLarger('hashId -> 'histId, inputHistograms)

  val distances = otherHashes.crossWithTiny(inputHashes)
    .map(('hashFrame, 'hashRef) -> 'distance) { x: (ImmutableBytesWritable, ImmutableBytesWritable) =>
      val (hashFrame, hashRef) = x

      Distance.hammingDistance(hashFrame.get, hashRef.get)
    }
    .groupAll { _.sortBy('distance) }

  // write out, mostly for debugging
  distances
    .write(Tsv(outputDistances, writeHeader = true, fields = ('distance, 'idFrame, 'idRef, 'secondFrame, 'secondRef, 'hashFrame, 'hashRef)))

  // group and find most similar movies
  distances
    .groupBy('idRef) {
      _.sum('distance -> 'totalDistance) // sum of distances = total distance from each movie to this one
    }
    .groupBy('idRef) {
      _.reverse.sortBy('totalDistance)
    }
    .write(Tsv(outputRanking, writeHeader = true, fields = ('distance, 'idFrame, 'idRef, 'secondFrame, 'secondRef)))

//  referenceHashes.crossWithTiny(frameHashes)
//    .map(('frameHash, 'refHash) -> ('frame, 'ref, 'distance)) { x: (ImmutableBytesWritable, ImmutableBytesWritable) =>
//      val (frame, reference) = x
//
//      (
//        frame.get.mkString(" "),
//        reference.get.mkString(" "),
//        Distance.hammingDistance(reference.get, frame.get)
//      )
//    }
//    .insert('inputId, FilenameUtils.getBaseName(inputId))
//    .groupAll {
//      _.sortBy('distance)
////      _.sortWithTake('distance -> 'out, TakeTopK) {
////        (d0: Int, d1: Int) => d0 < d1
////      }
//    }
//    .limit(takeTopK)
//    .write(Csv(output, writeHeader = true, fields = ('distance, 'id, 'inputId, 'frameHash, 'frame, 'refHash, 'ref)))


//  override val youtubeId = FilenameUtils.getBaseName(input)
//
//  Hashes
//    .read
////    .mapTo(('key, 'value) -> 'mhHash) { p: (Int, BytesWritable) => mhHash(p) }
////    .map('mhHash -> 'id) { h: ImmutableBytesWritable => youtubeId.asImmutableBytesWriteable } // because hbase Sink will cast to it, we need ALL fields as these
//  .map(('mhHash, 'youtube) -> 'pairs) {  }




/*
val things = List(1, 2, 5, 36, 23, 76) // this is a huge hbase table
val others = List(1, 2, 3) // this is a small (2000 items) collection

def similar(a: Int, b: Int): Double = a - b // this is a smart comparator, it returns "similar in 86.5%"

// I need to compare the small set, against all items from the huge table - one full scan
val matchOrNot = things flatMap { h =>
  others map { m => (h, m, similar(h, m)) }
}

// order by the ranking
val ranking = matchOrNot sortBy { - _._3 }

// get the best matches
val topMatches = ranking take 3

*/

}

