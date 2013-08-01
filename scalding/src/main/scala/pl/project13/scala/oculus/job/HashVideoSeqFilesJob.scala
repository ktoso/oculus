package pl.project13.scala.oculus.job

import com.twitter.scalding._

class HashVideoSeqFilesJob(args: Args) extends Job(args) {

  val inputFile = args("input")
  val _outputFile = args("output")

  val TableName = "frames"
  val TableSchema = 'youtube :: Nil

  type SeqFileElement = (Int, String)

  WritableSequenceFile(inputFile, ('key, 'value))
    .read
    .mapTo(('key, 'value) -> 'phash) { p: SeqFileElement => pHash(p._2) }
    .write(Tsv(_outputFile))

//    .write(
//      new HBaseSource(
//        TableName,
//        "quorum_name:2181",
//        'phash,
//        TableSchema.tail.map((x: Symbol) => "youtube"),
//        TableSchema.tail.map((x: Symbol) => new Fields(x.name)),
//        timestamp = Platform.currentTime
//      )
//    )

  // todo implement native call
  def pHash(bytes: String): String = {
    bytes.toString
  }

}



