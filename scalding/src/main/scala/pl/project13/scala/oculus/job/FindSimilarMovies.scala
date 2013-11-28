package pl.project13.scala.oculus.job

import com.twitter.scalding._
import pl.project13.scala.oculus.IPs
import pl.project13.scala.scalding.hbase.MyHBaseSource
import org.apache.commons.io.FilenameUtils
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import pl.project13.scala.oculus.distance.Distance
import com.twitter.scalding.typed.Joiner

class FindSimilarMovies(args: Args) extends Job(args)
  with PHashing {

  /** seq file with images */
  val inputMovie = args("input")

  val output = args("output")

  implicit val mode = Read

  val Hashes = new MyHBaseSource(
    tableName = "hashes",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'mhHash,
    familyNames = Array("youtube"),
    valueFields = Array('id)
  )

//  val frameHashes =
//    WritableSequenceFile(inputMovie, ('key, 'value))
//      .read
//      .mapTo(('key, 'value) -> 'frameHash) { p: SeqFileElement => mhHash(p) }

//  val hashes =
//    Hashes
//      .read
//      .project('mkHash)

  val frameHashes =
    IterableSource(List("coffebabe", "aaa", "bbb").map(_.asImmutableBytesWriteable), 'frameHash)

//  Hashes.project('referenceHash)
  IterableSource(List("aaa", "bananarama").map(_.asImmutableBytesWriteable), 'referenceHash)
    .crossWithTiny(frameHashes)
    .map(('frameHash, 'referenceHash) -> 'distance) { x: (ImmutableBytesWritable, ImmutableBytesWritable) =>
      val (frame, reference) = x
      Distance.hammingDistance(reference.get, frame.get)
    }
    .debug
    .groupAll {
      _.sortWithTake('distance -> 'out, k = 3) {
        (d0: Int, d1: Int) => try { d1 < d1 } catch { case ex => false }
      }
    }
    .debug
    .write(Tsv(output))


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

