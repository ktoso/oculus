package pl.project13.scala.oculus.actor

import akka.actor.{ActorLogging, Actor}
import pl.project13.scala.oculus.file.DownloadedVideoFile
import java.io.File
import org.apache.hadoop.fs.{Path, FileSystem}
import org.apache.hadoop.conf.Configuration

class HDFSUploadActor extends Actor
  with HDFSActions with ActorLogging {

  def receive = {
    case UploadToHDFS(local: DownloadedVideoFile) =>
      upload(local.file, createSourceVideoPath(local))

  }

  def createSourceVideoPath(local: DownloadedVideoFile): String = {
    "/source/" + local.fullName
  }
}

trait HDFSActions {

  val ConfigFiles = {
    val files = "hdfs" :: "core" :: Nil map { "/etc/hadoop/" + _ + "-site.xml" } map { new File(_) }
    assert(files.forall(_.exists), "All configuration files must exist! Check /etc/hadoop/")
    files
  }

  lazy val configuration = {
    val conf = new Configuration()
    ConfigFiles foreach { f => conf.addResource(f.getAbsolutePath) }
    conf
  }

  lazy val fileSystem =
    FileSystem.get(configuration)

  def upload(localPath: File, hdfsTarget: String, delSrc: Boolean = true, overwrite: Boolean = true) = {
    val from = new Path(localPath.getAbsolutePath)
    val to = new Path(hdfsTarget)

    fileSystem.mkdirs(to)
    fileSystem.copyFromLocalFile(delSrc, overwrite, from, to)
  }

  def delete(path: String) = {
    fileSystem.delete(new Path(path), true)
  }
}