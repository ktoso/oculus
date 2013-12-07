package pl.project13.scala.oculus.job

import com.twitter.scalding._
import pl.project13.scala.oculus.IPs
import pl.project13.scala.scalding.hbase.MyHBaseSource
import org.apache.commons.io.FilenameUtils
import org.apache.hadoop.io.IntWritable

class HistogramSeqFilesJob(args: Args) extends Job(args)
  with Histograms {

  val input = args("input")

  implicit val mode = Read

  val Hashes = new MyHBaseSource(
    tableName = "histograms",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'histogram,
    familyNames = Array("youtube"),
    valueFields = Array('id, 'frame)
  )

  override val youtubeId = FilenameUtils.getBaseName(input)

  WritableSequenceFile(input, ('key, 'value))
    .read
    .rename('key, 'frame)
    .map(('frame, 'value) -> ('id, 'histogram)) { p: SeqFileElement =>
      val histogram = mkHistogram(p)
      val luminance = histogram.getLuminanceHistogram
      val lumString = luminance.map(Integer.toHexString).mkString

      println("luminance (hex) = " + lumString)

      youtubeId.asImmutableBytesWriteable -> lumString.asImmutableBytesWriteable
    }
    .map('frame -> 'frame) { p: IntWritable => longToIbw(p.get) }
    .write(Hashes)

}

