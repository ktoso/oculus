  package pl.project13.scala.oculus.job

import com.twitter.scalding._
  import pl.project13.scala.oculus.conversions.WriteDOT

  class SortWithTakeJob(args: Args) extends Job(args)
  with WriteDOT {

  val inputFile = args("input")
  val outputFile = args("output")

  TextLine(inputFile)
    .read
    .flatMap('line -> 'word) { line: String => tokenize(line) }
    .groupBy('word) {
      _.sortWithTake('word -> 'size, 2) { (w1: String, w2: String) =>
        w1.length < w2.length
      }
    }
    .debug
    .write(Tsv(outputFile, writeHeader = true))


  def tokenize(text: String): Array[String] =
    text.toLowerCase.replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+")

}
