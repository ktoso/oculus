package pl.project13.scala.oculus.job

import com.twitter.scalding._
import pl.project13.scala.oculus.IPs
import pl.project13.scala.scalding.hbase.MyHBaseSource
import org.apache.commons.io.FilenameUtils
import org.apache.hadoop.io.IntWritable

class HashVideoSeqFilesJob(args: Args) extends Job(args)
  with TupleConversions
  with Histograms
  with Hashing {

  val input = args("input")

  implicit val mode = Read

  val Hashes = new MyHBaseSource(
    tableName = "hashes",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'mhHash,
    familyNames = Array("youtube", "youtube"),
    valueFields = Array('id,       'frame)
  )

  val Histograms = new MyHBaseSource(
    tableName = "histograms",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'lumHist,
    familyNames = Array("youtube", "youtube", "hist",   "hist",     "hist"),
    valueFields = Array('id,       'frame,    'redHist, 'greenHist, 'blueHist)
  )

  override val youtubeId = FilenameUtils.getBaseName(input)

  val calculateFlow = WritableSequenceFile(input, ('key, 'value))
    .read
    .rename('key, 'frame)

    .map(('frame, 'value) -> ('id, 'mhHash)) { p: SeqFileElement =>
      youtubeId.asImmutableBytesWriteable -> mhHash(p)
    }

    .map(('frame, 'value) -> ('id, 'lumHist, 'redHist, 'greenHist, 'blueHist)) { p: SeqFileElement =>
      val histogram = mkHistogram(p)
      val luminance = histogram.getLuminanceHistogram
      val red = histogram.getRedHistogram
      val green = histogram.getGreenHistogram
      val blue = histogram.getBlueHistogram

      val lumString = luminance.map(Integer.toHexString).mkString
      val redString = red.map(Integer.toHexString).mkString
      val greenString = green.map(Integer.toHexString).mkString
      val blueString = blue.map(Integer.toHexString).mkString

      (
        youtubeId.asImmutableBytesWriteable,
        lumString.asImmutableBytesWriteable,
        redString.asImmutableBytesWriteable,
        greenString.asImmutableBytesWriteable,
        blueString.asImmutableBytesWriteable
      )
    }

    .map('frame -> 'frame) { p: IntWritable => longToIbw(p.get) }

    calculateFlow
//      .discard('lumHistogram)
      .write(Hashes)

  calculateFlow
//    .discard('mhHash)
    .write(Histograms)

}

