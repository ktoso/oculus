package pl.project13.scala.oculus.hdfs

import pl.project13.scala.oculus.logging.Logging
import java.io.File
import org.apache.hadoop.fs.{Path, FileSystem}
import org.apache.hadoop.io.{IOUtils, Text, IntWritable, SequenceFile}
import java.net.URI
import org.apache.hadoop.io.SequenceFile.Writer

object AsSequenceFile extends Logging with HDFSActions {

  val conf = hdfsConfiguration

  def apply(targetUri: String, files: File*): Path = {

    val targetPath = new Path(targetUri)
    val fs = FileSystem.get(URI.create(targetUri), conf)

    var writer: Writer = null
    try {
      files.zipWithIndex foreach { case (file, i) =>
        val key = new IntWritable()
        val value = new Text

        writer = SequenceFile.createWriter(fs, conf, targetPath, key.getClass, value.getClass)

        val allBytesOfFile = io.Source.fromFile(file).map(_.toByte).toArray

        key.set(i)

        value.set(allBytesOfFile)
        writer.append(key, value)
      }

    } finally {
      IOUtils.closeStream(writer)
    }

    writer.close()

    targetPath
  }

}
