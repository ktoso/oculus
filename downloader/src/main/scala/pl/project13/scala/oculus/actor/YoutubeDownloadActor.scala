
package pl.project13.scala.oculus.actor

import akka.actor.{ActorLogging, Actor}
import pl.project13.scala.oculus.file.DownloadedVideoFile
import java.io.File
import com.google.common.io.Files

class YoutubeDownloadActor extends Actor
with YoutubeDownloadActions with ActorLogging {

  def receive = {
    case DownloadFromYoutube(url) =>
      log.info("Will download [%s]...".format(url))
      download(url)
      log.info("Finished downloading [%s]!".format(url))
  }
}

trait YoutubeDownloadActions {

  val OutputFilename = """.download. Destination: (.*)\.(.*)""".r

  val baseDir = {
    val f = new File("/data/youtube")
    Files.createParentDirs(f)
    f
  }

  import scala.sys.process._

  def download(url: String): Option[DownloadedVideoFile] = {
    val command =
      """| youtube-dl \
        | --no-progress
        | --continue
        | --title
        | --write-info-json
        | --prefer-free-formats \
        | %s    """.stripMargin.format(baseDir.getAbsolutePath, url)

    val cdToTargetDir = "cd " + baseDir.getAbsolutePath

    val output = (cdToTargetDir #| command).!!

    OutputFilename.findFirstMatchIn(output) map { m =>
      DownloadedVideoFile(baseDir, m.group(0), m.group(1))
    }
  }
}