
package pl.project13.scala.oculus.actor

import akka.actor.{ActorLogging, Actor}
import pl.project13.scala.oculus.file.DownloadedVideoFile
import sun.management.FileSystem

class HDFSUploadActor extends Actor
  with HDFSActions with ActorLogging {

  def receive = {
    case UploadToHDFS(local: DownloadedVideoFile) =>
      upload(local.path, "/source/" + local.fullName)

  }
}

trait HDFSActions {
  def upload(localPath: String, dhfsTarget: String) = {
    FileSystem.
  }
}