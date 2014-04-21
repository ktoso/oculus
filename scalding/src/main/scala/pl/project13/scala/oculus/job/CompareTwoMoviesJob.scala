package pl.project13.scala.oculus.job

import com.twitter.scalding._
import pl.project13.scala.oculus.IPs
import pl.project13.scala.scalding.hbase.{OculusStringConversions, MyHBaseSource}
import org.apache.commons.io.FilenameUtils
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import pl.project13.scala.oculus.distance.Distance
import org.apache.hadoop.io.{BytesWritable, IntWritable}
import pl.project13.scala.oculus.conversions.WriteDOT

class CompareTwoMoviesJob(args: Args) extends Job(args)
  with WriteDOT
  with OculusStringConversions
  with Hashing {

  val id1 = args("id1")
  val id2 = args("id2")

  val output = s"/oculus/compare-${id1}_AND_${id2}.out"
  

  // --- 

  val Hashes1 = new MyHBaseSource(
    tableName = "hashes",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'mhHash1,
    familyNames = Array("youtube", "youtube"),
    valueFields = Array('id,       'frame)
  )

  val Hashes2 = new MyHBaseSource(
    tableName = "hashes",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'mhHash2,
    familyNames = Array("youtube", "youtube"),
    valueFields = Array('id,       'frame)
  )

  val movie1 = 
    Hashes1
      .read
      .map('id -> 'id) { id: ImmutableBytesWritable => ibwToString(id) }
      .map('frame -> 'frame) { f : ImmutableBytesWritable => ibwToLong(f) }
      .filter('id) { id: String => id contains id1 }
      .rename('id -> 'id1)
      .rename('frame -> 'frame1)

  val movie2 = 
    Hashes2
      .read
      .map('id -> 'id) { id: ImmutableBytesWritable => ibwToString(id) }
      .map('frame -> 'frame) { f : ImmutableBytesWritable => ibwToLong(f) }
      .filter('id) { id: String => id contains id2 }
      .rename('id -> 'id2)
      .rename('frame -> 'frame2)

  movie1.write(Csv(s"/oculus/raw-$id1"))

  movie2.write(Csv(s"/oculus/raw-$id2"))

  
  movie1.joinWithSmaller('frame1 -> 'frame2, movie2)
    .map(('mhHash1, 'mhHash2) -> ('hash1, 'hash2, 'distance)) { x: (ImmutableBytesWritable, ImmutableBytesWritable) =>
      val (h1, h2) = x

      (
        h1.get.mkString(""),
        h2.get.mkString(""),
        Distance.hammingDistance(h1.get, h2.get)
      )
    }
    .groupAll {
      _.sortBy('distance)
    }
    .write(Csv(output, writeHeader = true, fields = ('distance, 'id1, 'id2, 'frame1, 'frame2, 'hash1, 'hash2)))

}

