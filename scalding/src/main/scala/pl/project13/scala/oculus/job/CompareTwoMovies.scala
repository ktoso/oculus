package pl.project13.scala.oculus.job

import com.twitter.scalding._
import pl.project13.scala.oculus.IPs
import pl.project13.scala.scalding.hbase.MyHBaseSource
import org.apache.commons.io.FilenameUtils
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import pl.project13.scala.oculus.distance.Distance
import org.apache.hadoop.io.IntWritable

class CompareTwoMovies(args: Args) extends Job(args)
  with Hashing {

  /** seq file with images */
  val inputMovie1 = args("input1")

  /** seq file with images */
  val inputMovie2 = args("input2")

  val output = args("output")
  

  // --- 

  val id1 = inputMovie1.asImmutableBytesWriteable
  val id2 = inputMovie2.asImmutableBytesWriteable

  val movie1 = 
    WritableSequenceFile(inputMovie1, ('key1, 'value))
      .read
      .map(('key1, 'value) -> ('id1, 'frameHash1)) { p: SeqFileElement => id1 -> mhHash(p) }
      .discard('value)

  val movie2 = 
    WritableSequenceFile(inputMovie2, ('key2, 'value))
      .read
      .map(('key2, 'value) -> ('id2, 'frameHash2)) { p: SeqFileElement => id2 -> mhHash(p) }
      .discard('value)
    
  
  movie1.joinWithLarger('key1 -> 'key2, movie2)
    .map(('frameHash1, 'frameHash2) -> ('h1, 'h2, 'distance)) { x: (ImmutableBytesWritable, ImmutableBytesWritable) =>
      val (h1, h2) = x

      (
        h1.get.mkString(" "),
        h2.get.mkString(" "),
        Distance.hammingDistance(h1.get, h2.get)
      )
    }
    .groupAll {
      _.sortBy('distance)
    }
    .write(Csv(output, writeHeader = true, fields = ('distance, 'id1, 'id2, 'h1, 'h2)))

}

