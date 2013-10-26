package pl.project13.scala.oculus.hdfs

import java.io.File
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{Path, FileSystem}
import com.typesafe.config.{ConfigFactory, Config}
import collection.JavaConversions._
import com.typesafe.scalalogging.slf4j.Logging

trait HDFSActions extends Logging {

  /** Override if you need other than default settings - loads up ''application.conf'' */
  def configuration: Config = ConfigFactory.load()

  val requireDistributedMode = true

  lazy val hdfsConfiguration = {
    logger.info("Configuring HDFS connection...")
    val conf = new Configuration(true)

    allOculusHadoopSettings(configuration) foreach { case (key, value) =>
      logger.info("Configuring [%s]: %s".format(key, value))

      conf.setStrings(key, value.unwrapped.toString)
    }

    conf
  }

  lazy val fileSystem = {
    logger.info("Preparing HDFS FileSystem instance...")
    val fs = FileSystem.get(hdfsConfiguration)

    Runtime.getRuntime.addShutdownHook(new Thread(){
      override def run() {
        logger.info("Shutting down HDFS FileSystem connection...")
        fs.close()
      }
    })

    fs
  }

  def uploadAsSequenceFile(localFiles: List[File], hdfsTarget: String, delSrc: Boolean = true, overwrite: Boolean = true) = {
    logger.info("Uploading [%s] files as packed sequence file [%s]...".format(localFiles.length, hdfsTarget))
    AsSequenceFile.uploadPackedImages(hdfsTarget, localFiles: _*)
  }

  /**
   * @return Path referncing the uploaded file (on HDFS)
   */
  def upload(localPath: File, hdfsTarget: String, delSrc: Boolean, overwrite: Boolean = true) = {
    logger.info("Uploading [%s] to HDFS path [%s]...".format(localPath.getAbsolutePath, hdfsTarget))

    val from = new Path(localPath.getAbsolutePath)
    val to = new Path(hdfsTarget)

    logger.info(s"Uploading from = $from")
    logger.info(s"Uploading to = $to")

    // remember to mkdir /oculus/source
    fileSystem.copyFromLocalFile(delSrc, overwrite, from, to)

    to
  }

  def delete(path: String) = {
    fileSystem.delete(new Path(path), true)
  }

  private def allOculusHadoopSettings(configuration: com.typesafe.config.Config) =
    configuration.getConfig("oculus").getConfig("hadoop").entrySet().toList.map(a => (a.getKey, a.getValue))

}
