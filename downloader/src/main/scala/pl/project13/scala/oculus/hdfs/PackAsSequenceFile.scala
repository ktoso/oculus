package pl.project13.scala.oculus.hdfs

import pl.project13.scala.oculus.logging.Logging
import java.io.File
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{Path, FileSystem}
import com.typesafe.config.{ConfigFactory, Config}
import collection.JavaConversions._
import org.apache.hadoop.io.SequenceFile

object PackAsSequenceFile extends Logging {


  def apply(files: File*): SequenceFile = {
    null
  }

}
