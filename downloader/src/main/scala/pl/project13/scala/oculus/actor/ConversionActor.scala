package pl.project13.scala.oculus.actor

import akka.actor.{ActorRef, ActorLogging, Actor}
import pl.project13.scala.oculus.file.DownloadedVideoFile
import pl.project13.scala.oculus.hdfs.{HBaseActions, HDFSActions}
import pl.project13.scala.oculus.ffmpeg.FFMPEG
import java.io.File
import pl.project13.scala.rainbow._

class ConversionActor(hdfsUploadActor: ActorRef, hdfsLocation: String, fps: Int) extends Actor with ActorLogging
  with HBaseActions {

  def receive = {
    case Convert(local: DownloadedVideoFile) if local.file.exists =>
      log.info("Got request to upload as sequence file [%s] to HDFS...".format(local.fullName))

      val images = FFMPEG.ffmpegToImages(local.file, framesPerSecond = fps)
      storeMetadataFor(local)
      local.file.delete()

      hdfsUploadActor ! UploadAsSequenceFileToHDFS(local)

      cleanupDownloadedFiles(local, images)
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