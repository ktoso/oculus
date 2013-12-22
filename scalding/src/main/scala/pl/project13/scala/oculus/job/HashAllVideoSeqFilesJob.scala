package pl.project13.scala.oculus.job

import com.twitter.scalding._
import pl.project13.scala.oculus.IPs
import pl.project13.scala.scalding.hbase.MyHBaseSource
import org.apache.commons.io.FilenameUtils
import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.fs.{Path, FileSystem}
import org.apache.hadoop.conf.Configuration
import collection.JavaConversions._
import com.typesafe.config.{ConfigFactory, Config}

class HashAllVideoSeqFilesJob(args: Args) extends Job(args)
  with TupleConversions
  with Hashing {
  
  // -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------
  /** Override if you need other than default settings - loads up ''application.conf'' */
  def appConfig: Config = ConfigFactory.load()
  
  private def allOculusHadoopSettings(configuration: com.typesafe.config.Config) =
        configuration.getConfig("oculus").getConfig("hadoop").entrySet().toList.map(a => (a.getKey, a.getValue))

  val conf = new Configuration(false)
  conf.setStrings("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem")
    allOculusHadoopSettings(appConfig) foreach { case (key, value) =>
      conf.setStrings(key, value.unwrapped.toString)
    }
  
  val fs = FileSystem.get(conf)
  val seqFiles = fs.listStatus(new Path("hdfs:///oculus/source/")).map(_.getPath.toString)
  
  // -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------
  
  implicit val mode = Read

  val Hashes = new MyHBaseSource(
    tableName = "hashes",
    quorumNames = IPs.HadoopMasterWithPort,
    keyFields = 'mhHash,
    familyNames = Array("youtube", "youtube"),
    valueFields = Array('id, 'frame)
  )

  override var youtubeId = "???"



  seqFiles foreach { hashingJob }


  def hashingJob(input: String) = {

    youtubeId = FilenameUtils.getBaseName(input)
    WritableSequenceFile(input, ('key, 'value))
      .read
      .rename('key, 'frame)
      .map(('frame, 'value) -> ('id, 'mhHash)) { p: SeqFileElement =>
        youtubeId.asImmutableBytesWriteable -> mhHash(p)
      }
      .map('frame -> 'frame) { p: IntWritable => longToIbw(p.get) }
      .write(Hashes)
  }

}

