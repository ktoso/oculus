package pl.project13.scala.oculus.job

import com.twitter.scalding._
import parallelai.spyglass.hbase.HBaseConstants.SourceMode
import parallelai.spyglass.hbase.HBaseSource

class MostSimiliarImagesJob(args: Args) extends Job(args) {

  val inputFile = args("input")
  val outputFile = args("output")

  val hbs2 = new HBaseSource(
    "table_name",
    "quorum_name:2181",
    'key,
    Array("column_family"),
    Array('column_name),
    sourceMode = SourceMode.SCAN_ALL)

}
