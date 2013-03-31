package pl.project13.scala.oculus.job

import com.twitter.scalding._
import pl.project13.scala.scalding.hbase.HBaseSource
import cascading.tuple.Fields

class WordCountJob(args: Args) extends Job(args) {

  TextLine(args("input"))
    .flatMap('line -> 'word) { line: String => tokenize(line) }
    .groupBy('word) { _.size }
    .write(Tsv(args("output")))

  // Split a piece of text into individual words.
  def tokenize(text: String): Array[String] = {
    // Lowercase each word and remove punctuation.
    text.toLowerCase.replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+")
  }

}
