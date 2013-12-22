package pl.project13.scala.oculus.job

import com.twitter.scalding._
import pl.project13.scala.oculus.IPs
import pl.project13.scala.scalding.hbase.MyHBaseSource
import org.apache.commons.io.FilenameUtils
import org.apache.hadoop.io.IntWritable

class HashVideoSeqFilesJob(args: Args) extends Job(args)
  with TupleConversions
  with Hashing {

  val input = args("input")

  implicit val mode = Read

  val Hashes = new MyHBaseSource(
    tableName = "hashes",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'mhHash,
    familyNames = Array("youtube", "youtube"),
    valueFields = Array('id, 'frame)
  )

  override val youtubeId = FilenameUtils.getBaseName(input)

  WritableSequenceFile(input, ('key, 'value))
    .read
    .rename('key, 'frame)
    .map(('frame, 'value) -> ('id, 'mhHash)) { p: SeqFileElement =>
      youtubeId.asImmutableBytesWriteable -> "010101".asImmutableBytesWriteable // mhHash(p)
    }
    .map('frame -> 'frame) { p: IntWritable => longToIbw(p.get) }
    .write(Hashes)

}

