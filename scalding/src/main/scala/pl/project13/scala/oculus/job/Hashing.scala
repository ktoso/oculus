package pl.project13.scala.oculus.job

import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import pl.project13.scala.oculus.phash.PHash
import java.io.File
import com.google.common.io.Files
import org.apache.hadoop.io.BytesWritable
import pl.project13.scala.oculus.conversions.OculusTupleConversions
import pl.project13.scala.scalding.hbase.OculusStringConversions
import pl.project13.common.utils.Histogram

trait Hashing extends OculusTupleConversions with OculusStringConversions {

  def youtubeId: String = "???"

//  type SeqFileElement = (Int, BytesWritable)

  // todo do the same with dct hash!!!!!
  def mhHashString(seqFileEl: (Int, BytesWritable)) = {
    val (idx, bytes) = seqFileEl
    val bs = bytes.getBytes

    println(s"processing element [$idx] of sequence file [$youtubeId]. [size: ${bs.size}]")

    val result = onTmpFile(bs) {
      f => PHash.analyzeImage(f)
    }

    result.mhHash
  }

  def mhHash(seqFileEl: (Int, BytesWritable)): ImmutableBytesWritable =
    mhHashString(seqFileEl).asImmutableBytesWriteable

  def onTmpFile[T](bytes: Array[Byte])(block: File => T): T = {
    val f = File.createTempFile("oculus-hashing", ".png")
    try {
      Files.write(bytes, f)
      block(f)
    } finally {
      f.delete()
    }
  }

}