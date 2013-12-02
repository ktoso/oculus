package pl.project13.scala.oculus.job

import com.twitter.scalding._
import pl.project13.scala.oculus.IPs
import pl.project13.scala.scalding.hbase.MyHBaseSource
import org.apache.commons.io.FilenameUtils
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import pl.project13.scala.oculus.distance.Distance
import com.twitter.scalding.typed.Joiner
import pl.project13.scala.oculus.phash.PHash

class FindSimilarMovies(args: Args) extends Job(args)
  with PHashing {

  /** seq file with images */
  val inputMovie = args("input")

  val output = args("output")

  implicit val mode = Read

  val Hashes = new MyHBaseSource(
    tableName = "hashes",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'refHash,
    familyNames = Array("youtube"),
    valueFields = Array('id, 'second)
  )

  var takeTopK = 0

  val frameHashes =
    WritableSequenceFile(inputMovie, ('key, 'value))
      .read
      .limit(3)
      .mapTo(('key, 'value) -> 'frameHash) { p: SeqFileElement =>
        takeTopK += 1 // we want to take as many top matches as we have movies
        mhHash(p)
      }

  val referenceHashes =
    Hashes
      .read
      .map('id -> 'id) { id: String => id.spaceSeparatedHexCodesToString }
//      .limit(5000)

  referenceHashes.crossWithTiny(frameHashes)
    .map(('frameHash, 'refHash) -> ('frame, 'ref, 'distance)) { x: (ImmutableBytesWritable, ImmutableBytesWritable) =>
      val (frame, reference) = x

      (
        frame.get.mkString(" "),
        reference.get.mkString(" "),
        Distance.hammingDistance(reference.get, frame.get)
      )
    }
    .insert('inputId, FilenameUtils.getBaseName(inputMovie))
    .groupAll {
      _.sortBy('distance)
//      _.sortWithTake('distance -> 'out, TakeTopK) {
//        (d0: Int, d1: Int) => d0 < d1
//      }
    }
    .limit(takeTopK)
    .write(Csv(output, writeHeader = true, fields = ('distance, 'id, 'inputId, 'frameHash, 'frame, 'refHash, 'ref)))


//  override val youtubeId = FilenameUtils.getBaseName(input)
//
//  Hashes
//    .read
////    .mapTo(('key, 'value) -> 'mhHash) { p: SeqFileElement => mhHash(p) }
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
