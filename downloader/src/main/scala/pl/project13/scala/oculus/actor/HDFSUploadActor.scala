package pl.project13.scala.oculus.actor

import akka.actor.{ActorLogging, Actor}
import pl.project13.scala.oculus.file.DownloadedVideoFile
import pl.project13.scala.oculus.hdfs.HDFSActions

class HDFSUploadActor extends Actor with ActorLogging
  with HDFSActions {

  def receive = {
    case UploadToHDFS(local: DownloadedVideoFile) =>
      log.info("Will upload [%s] to HDFS...".format(local.fullName))
      upload(local.file, createTargetPath(local))
  }

  def createTargetPath(local: DownloadedVideoFile): String = {
    "/oculus/source/" + local.fullName
      .replaceAll("'", "")
      .replaceAll(" ", "_")
  }

}