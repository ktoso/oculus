  package pl.project13.scala.oculus.job

import com.twitter.scalding._
  import pl.project13.scala.oculus.conversions.WriteDOT

  class WordCountJob(args: Args) extends Job(args)
  with WriteDOT {

  val inputFile = args("input")
  val outputFile = args("output")

    IterableSource(
      List(
        (1, 2, 3),
        (1, 2, 3)
      )
    )

//  TextLine(inputFile)
//    .read
//    .flatMap('line -> 'word) { line: String => tokenize(line) }
//    .groupBy('word) { _.size }
//    .write(Tsv(outputFile))


  def tokenize(text: String): Array[String] =
    text.toLowerCase.replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+")

}
