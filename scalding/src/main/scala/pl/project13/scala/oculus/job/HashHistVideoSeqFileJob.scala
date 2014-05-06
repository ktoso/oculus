package pl.project13.scala.oculus.job

import com.twitter.scalding._
import org.apache.commons.io.FilenameUtils
import org.apache.hadoop.io.{BytesWritable, IntWritable}
import pl.project13.scala.oculus.conversions.WriteDOT
import pl.project13.scala.oculus.source.hbase.{HistogramsSource, HashesSource}

class HashHistVideoSeqFileJob(args: Args) extends Job(args) with TupleConversions
  with WriteDOT
  with HashesSource with HistogramsSource
  with Histograms with Hashing {

  val input = args("input")

  implicit val mode = Read

  override val youtubeId = FilenameUtils.getBaseName(input)

  WritableSequenceFile(input, ('key, 'value))
    .read
    .rename('key, 'frame)

    .map(('frame, 'value) -> ('key, 'id, 'dct, 'mh, 'lumHist, 'redHist, 'greenHist, 'blueHist)) { p: (Int, BytesWritable) =>
      val (dominating, histogram) = mkHistogram(p)

      val luminance = histogram.getLuminanceHistogram
      val red = histogram.getRedHistogram
      val green = histogram.getGreenHistogram
      val blue = histogram.getBlueHistogram

      val lumString = luminance.map(Integer.toHexString).mkString
      val redString = red.map(Integer.toHexString).mkString
      val greenString = green.map(Integer.toHexString).mkString
      val blueString = blue.map(Integer.toHexString).mkString
    
      val (dct, mh) = dct_mh(p)

      val key = new StringBuilder(dominating.key).append(":").append(youtubeId).append(":").append(p._1).toString()

      (
        key.asImmutableBytesWriteable,
        youtubeId.asImmutableBytesWriteable,
        dct,
        mh,
        lumString.asImmutableBytesWriteable,
        redString.asImmutableBytesWriteable,
        greenString.asImmutableBytesWriteable,
        blueString.asImmutableBytesWriteable
      )
    }

    .map('frame -> 'frame) { p: IntWritable => longToIbw(p.get) }
//    .write(HashesTable)
    .write(HistogramsTable)

}

