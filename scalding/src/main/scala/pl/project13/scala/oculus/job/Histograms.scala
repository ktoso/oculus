package pl.project13.scala.oculus.job

import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import pl.project13.scala.oculus.phash.PHash
import java.io.File
import com.google.common.io.Files
import org.apache.hadoop.io.BytesWritable
import pl.project13.scala.oculus.conversions.OculusTupleConversions
import pl.project13.scala.scalding.hbase.OculusStringConversions
import pl.project13.common.utils.Histogram

trait Histograms extends OculusTupleConversions with OculusStringConversions {

//  type SeqFileElement = (Int, BytesWritable)

  def mkHistogram(seqFileEl: (Int, BytesWritable)): Histogram = {
    val (idx, bytes) = seqFileEl
    val bs = bytes.getBytes

    val result = onTmpFile(bs) { f =>
      Histogram.getHisrogram(f.getAbsolutePath)
    }

    result
  }

  private def onTmpFile[T](bytes: Array[Byte])(block: File => T): T = {
    val f = File.createTempFile("oculus-histograms", ".png")
    try {
      Files.write(bytes, f)
      block(f)
    } finally {
      f.delete()
    }
  }

}
