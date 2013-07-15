package pl.project13.scala.oculus.hdfs

import java.io.File
import org.apache.hadoop.fs.{Path, FileSystem}
import org.apache.hadoop.io.{IOUtils, Text, IntWritable, SequenceFile}
import java.net.URI
import org.apache.hadoop.io.SequenceFile.Writer
import com.google.common.io.Files
import com.typesafe.scalalogging.slf4j.Logging

object AsSequenceFile extends Logging with HDFSActions {

  import pl.project13.scala.oculus.common.noun.PercentageNoun._

  val conf = hdfsConfiguration

  val ExtractNumber = """image(\d+)\.png""".r

  /**
   * Uploads many images as one big SequenceFile.
   *
   * Assumes the files to be named like: `image#####.png`.
   */
  def uploadPackedImages(targetUri: String, files: File*): Path = {
    logger.info("Will combine [%s] files and upload as SequenceFile [%s]".format(files.size, targetUri))

    val targetPath = new Path(targetUri)
    val fs = FileSystem.get(URI.create(targetUri), conf)

    var writer: Writer = null
    try {
      val key = new IntWritable
      val value = new Text

      writer = SequenceFile.createWriter(fs, conf, targetPath, key.getClass, value.getClass)

      var processed = 0
      val all = files.size
      files foreach { file =>
        val ExtractNumber(indexStr) = file.getName

        try {
          val bytes = Files.toByteArray(file)

          key.set(indexStr.toInt)

          value.set(bytes)
          writer.append(key, value)

          processed += 1
          logger.info("Appended [%s] info SequenceFile [%s], %s".format(file.getName, targetUri, processed outOf all))
        } catch {
          case ex: Exception =>
            logger.warn("Unable to put [%s] file into SequenceFile...".format(file.getAbsolutePath), ex)
            IOUtils.closeStream(writer)
        }
      }

    } finally {
      IOUtils.closeStream(writer)
    }

    targetPath
  }

//  example logs
//  11/Apr/13 02:16:32.723 [oculus-system-akka.actor.default-dispatcher-5] INFO  p.p.s.oculus.hdfs.AsSequenceFile$ - Appended image [1149][image1149.png] info SequenceFile [/oculus/source/yCj_Xv6XKIM.webm.seq]
//  11/Apr/13 02:16:35.960 [oculus-system-akka.actor.default-dispatcher-5] INFO  p.p.s.oculus.hdfs.AsSequenceFile$ - Appended image [1159][image1159.png] info SequenceFile [/oculus/source/yCj_Xv6XKIM.webm.seq]
//  11/Apr/13 02:16:37.169 [oculus-system-akka.actor.default-dispatcher-3] INFO  p.p.s.oculus.hdfs.AsSequenceFile$ - Appended image [1029][image1029.png] info SequenceFile [/oculus/source/FOq1_aPmlbE.webm.seq]
//  11/Apr/13 02:16:38.868 [oculus-system-akka.actor.default-dispatcher-5] INFO  p.p.s.oculus.hdfs.AsSequenceFile$ - Appended image [1169][image1169.png] info SequenceFile [/oculus/source/yCj_Xv6XKIM.webm.seq]
//  11/Apr/13 02:16:41.333 [oculus-system-akka.actor.default-dispatcher-5] INFO  p.p.s.oculus.hdfs.AsSequenceFile$ - Appended image [1179][image1179.png] info SequenceFile [/oculus/source/yCj_Xv6XKIM.webm.seq]

}
