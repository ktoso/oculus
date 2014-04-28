package pl.project13.scala.oculus.actor

import akka.actor.{ActorLogging, Actor}
import pl.project13.scala.oculus.file.DownloadedVideoFile
import pl.project13.scala.oculus.hdfs.{HBaseActions, HDFSActions}
import pl.project13.scala.oculus.ffmpeg.FFMPEG
import java.io.File
import pl.project13.scala.rainbow._

class HDFSUploadActor(hdfsLocation: String, fps: Int) extends Actor with ActorLogging
  with HDFSActions
  with HBaseActions {

  def receive = {
    case RequestUploadToHDFS(local: DownloadedVideoFile) =>
//      not required to store entire video
      self forward UploadAsSequenceFileToHDFS(local)


    case UploadAsSequenceFileToHDFS(local: DownloadedVideoFile) if local.file.exists =>
      log.info("Got request to upload as sequence file [%s] to HDFS...".format(local.fullName))

      val images = FFMPEG.ffmpegToImages(local.file, framesPerSecond = fps)
      uploadAsSequenceFile(images, createTargetPath(local) + ".seq")
      storeMetadataFor(local)

      cleanupDownloadedFiles(local, images)
      sender ! ConfirmUploadOf(local)

    case UploadAsSequenceFileToHDFS(local: DownloadedVideoFile) if !local.file.exists =>
      log.warning("Got request to upload not existing file[%s] to HDFS! Ignoring...".format(local.fullName))


    case UploadFileToHDFS(local: DownloadedVideoFile) if local.file.exists =>
      log.info("Will upload plain file [%s] to HDFS...".format(local.fullName))
      upload(local.file, createTargetPath(local), delSrc = true)
      storeMetadataFor(local)
      log.info(s"Finished uploading file ${local.file.getAbsolutePath} into HDFS...".green)

    case UploadFileToHDFS(local: DownloadedVideoFile) if !local.file.exists =>
      log.warning(s"Got request to upload ${local.file}, but it does not exists! Ignoring...")
  }


  def cleanupDownloadedFiles(local: DownloadedVideoFile, images: List[File]) {
    logger.info("Cleaning up after upload of [%s] based sequence file".format(local.file.getName))
    local.file :: images foreach { _.delete() }
    logger.info("Cleaned up after [%s] uploaded file.".format(local.file.getName))
  }

  def createTargetPath(local: DownloadedVideoFile): String =
    hdfsLocation + "/oculus/source/" + local.fullName

  def delete(local: DownloadedVideoFile) {
    logger.info(s"Deleting files for [${local.file.getName}}]")
    local.file.delete()
    local.infoFile.delete()
  }

}