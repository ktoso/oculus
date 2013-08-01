package pl.project13.scala.oculus.job

import com.twitter.scalding._

class WordCountJob(args: Args) extends Job(args) {

  val inputFile = args("input")
  val outputFile = args("output")

  TextLine(inputFile)
    .read
    .flatMap('line -> 'word) { line: String => tokenize(line) }
    .groupBy('word) { _.size }
    .write(Tsv(outputFile))


  def tokenize(text: String): Array[String] = {
    // Lowercase each word and remove punctuation.
    println(text)
    text.toLowerCase.replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+")
  }

}
