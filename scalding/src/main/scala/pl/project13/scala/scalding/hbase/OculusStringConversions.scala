package pl.project13.scala.scalding.hbase

import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import com.google.common.base.Charsets
import org.apache.hadoop.hbase.util.Bytes

trait OculusStringConversions {

  implicit def asRichString(s: String): RichString = new RichString(s)

  implicit def bytesToString(bytes: Array[Byte]): String = Bytes.toString(bytes)
  implicit def longToIbw(l: Long): ImmutableBytesWritable = stringToIbw(l.toString)
  implicit def bytesToLong(bytes: Array[Byte]): Long = augmentString(bytesToString(bytes)).toLong
  implicit def ibwToString(ibw: ImmutableBytesWritable): String = bytesToString(ibw.get())
  implicit def ibwToLong(ibw: ImmutableBytesWritable): Long = bytesToString(ibw.get()).toLong
  implicit def stringToIbw(s: String):ImmutableBytesWritable = new ImmutableBytesWritable(Bytes.toBytes(s))

}

case class RichString(s: String) extends AnyVal {
  def asImmutableBytesWriteable: ImmutableBytesWritable =
    new ImmutableBytesWritable(s.getBytes(Charsets.UTF_8))

  def spaceSeparatedHexCodesToString: String =
    new String(s.split(" ").map { s => Integer.parseInt(s, 16).toByte })
}