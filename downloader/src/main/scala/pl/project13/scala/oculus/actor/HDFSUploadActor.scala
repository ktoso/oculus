package pl.project13.scala.oculus.actor

import akka.actor.{ActorLogging, Actor}
import pl.project13.scala.oculus.file.DownloadedVideoFile
import pl.project13.scala.oculus.hdfs.{HBaseActions, HDFSActions}
import pl.project13.scala.oculus.ffmpeg.FFMPEG
import java.io.File

class HDFSUploadActor(hdfsLocation: String) extends Actor with ActorLogging
  with HDFSActions
  with HBaseActions {

  def receive = {
    case RequestUploadToHDFS(local: DownloadedVideoFile) =>
      self ! UploadFileToHDFS(local)
      self ! UploadAsSequenceFileToHDFS(local)

    case UploadAsSequenceFileToHDFS(local: DownloadedVideoFile) if local.file.exists =>
      log.info("Got request to upload as sequence file [%s] to HDFS...".format(local.fullName))

      val images = FFMPEG.ffmpegToImages(local.file, framesPerSecond = 10)
      uploadAsSequenceFile(images, createTargetPath(local) + ".seq")

      cleanupDownloadedFiles(local, images)

    case UploadAsSequenceFileToHDFS(local: DownloadedVideoFile) if !local.file.exists =>
      log.info("Got request to upload not existing file[%s] to HDFS! Ignoring...".format(local.fullName))

    case UploadFileToHDFS(local: DownloadedVideoFile) if local.file.exists =>
      log.info("Will upload plain file [%s] to HDFS...".format(local.fullName))
      upload(local.file, createTargetPath(local), delSrc = true)
      storeMetadataFor(local)
      delete(local)

    case UploadFileToHDFS(local: DownloadedVideoFile) =>
      log.info(s"Got request to upload ${local.file}, but it does not exists! Ignoring...")
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