package pl.project13.scala.oculus.hdfs

import com.typesafe.scalalogging.slf4j.Logging
import pl.project13.scala.oculus.hbase.tables.PropsSchema
import pl.project13.scala.oculus.file.DownloadedVideoFile
import java.io.File
import com.google.common.base.Charsets
import pl.project13.scala.oculus.youtubedl.YouTubeMeta
import org.joda.time.DateTime

trait HBaseActions extends Logging {

  val Props = PropsSchema.MetadataTable

  def storeMetadataFor(f: DownloadedVideoFile) = {
    val (json, metadata) = getMetadata(f)

    logger.info(s"Putting metadata for [${metadata.id}]")
    Props.put(metadata.id)
      .value(_.json, json)
      .value(_.crawledAt, new DateTime)
      .execute()

    metadata
  }

  def getMetadata(f: DownloadedVideoFile): (String, YouTubeMeta) = {
    val source = io.Source.fromFile(f.infoFile, Charsets.UTF_8.name)

    val json = source.getLines().mkString
    val metadata = YouTubeMeta.from(json)
    source.close()
    json -> metadata
  }
}
