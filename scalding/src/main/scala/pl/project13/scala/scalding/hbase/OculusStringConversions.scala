package pl.project13.scala.scalding.hbase

import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import com.google.common.base.Charsets

trait OculusStringConversions {

  implicit def asRichString(s: String): RichString = new RichString(s)

}

class RichString(s: String) extends AnyVal {
    def asImmutableBytesWriteable: ImmutableBytesWritable =
      new ImmutableBytesWritable(s.getBytes(Charsets.UTF_8))

    def spaceSeparatedHexCodesToString: String =
      new String(s.split(" ").map { s => Integer.parseInt(s, 16).toByte })
  }