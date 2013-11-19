package pl.project13.scala.scalding.hbase

import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import com.google.common.base.Charsets

trait OculusStringConversions {
  implicit class RichString(s: String) {
    def asImmutableBytesWriteable: ImmutableBytesWritable =
      new ImmutableBytesWritable(s.getBytes(Charsets.UTF_8))
  }
}