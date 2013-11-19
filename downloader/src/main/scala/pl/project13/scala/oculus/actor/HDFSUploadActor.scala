package pl.project13.scala.oculus.actor

import akka.actor.{ActorLogging, Actor}
import pl.project13.scala.oculus.file.DownloadedVideoFile
import pl.project13.scala.oculus.hdfs.{HBaseActions, HDFSActions}
import pl.project13.scala.oculus.ffmpeg.FFMPEG
import java.io.File
import pl.project13.scala.rainbow._
import akka.pattern.AskSupport
import scala.concurrent.{Future, Await}
import akka.util.Timeout
import scala.concurrent.duration._

class HDFSUploadActor(hdfsLocation: String) extends Actor with ActorLogging
  with AskSupport
  with HDFSActions
  with HBaseActions {

  implicit val timeout = Timeout(5 minutes)

  def receive = {
    case RequestUploadToHDFS(local: DownloadedVideoFile) =>
      val me = self
      val f1 = me ? UploadFileToHDFS(local)
      val f2 = me ? UploadAsSequenceFileToHDFS(local)

      Future.sequence(List(f1, f2)) onComplete { f =>
        me ! CleanupAfterUploads(local)
      }

    case UploadAsSequenceFileToHDFS(local: DownloadedVideoFile) if local.file.exists =>
      log.info("Got request to upload as sequence file [%s] to HDFS...".format(local.fullName))

      val images = FFMPEG.ffmpegToImages(local.file, framesPerSecond = 10)
      uploadAsSequenceFile(images, createTargetPath(local) + ".seq")

      cleanupDownloadedFiles(local, images)

    case UploadAsSequenceFileToHDFS(local: DownloadedVideoFile) if !local.file.exists =>
      log.warning("Got request to upload not existing file[%s] to HDFS! Ignoring...".format(local.fullName))

    case UploadFileToHDFS(local: DownloadedVideoFile) if local.file.exists =>
      log.info("Will upload plain file [%s] to HDFS...".format(local.fullName))
      upload(local.file, createTargetPath(local), delSrc = true)
      storeMetadataFor(local)
      log.info(s"Finished uploading file ${local.file.getAbsolutePath} into HDFS...".green)

    case UploadFileToHDFS(local: DownloadedVideoFile) =>
      log.warning(s"Got request to upload ${local.file}, but it does not exists! Ignoring...")
  }


  def cleanupDownloadedFiles(local: DownloadedVideoFile, images: List[File]) {
    logger.info("Cleaning up after upload of [%s] based sequence file".format(local.file.getName))
    local.file :: images foreach { _.delete() }
  }

  def createTargetPath(local: DownloadedVideoFile): String =
    hdfsLocation + "/oculus/source/" + local.fullName

  def delete(local: DownloadedVideoFile) {
    logger.info(s"Deleting files for [${local.file.getName}}]")
    local.file.delete()
    local.infoFile.delete()
  }

}