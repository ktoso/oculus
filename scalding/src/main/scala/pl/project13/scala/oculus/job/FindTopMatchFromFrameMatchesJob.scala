package pl.project13.scala.oculus.job

import com.twitter.scalding._
import pl.project13.scala.oculus.IPs
import pl.project13.scala.scalding.hbase.MyHBaseSource
import org.apache.commons.io.FilenameUtils
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import pl.project13.scala.oculus.distance.Distance
import com.twitter.scalding.typed.Joiner
import pl.project13.scala.oculus.phash.PHash
import org.apache.hadoop.io.BytesWritable
import pl.project13.scala.oculus.conversions.{WriteDOT, OculusRichPipe}

class FindTopMatchFromFrameMatchesJob(args: Args) extends Job(args)
  with WriteDOT
  with OculusRichPipe
  with Histograms
  with Hashing {

  val inputId = args("id")
  val input = args("input")

  Csv(input)

  ???

}