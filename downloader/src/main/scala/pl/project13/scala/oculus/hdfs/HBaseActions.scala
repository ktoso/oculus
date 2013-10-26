package pl.project13.scala.oculus.hdfs

import com.typesafe.scalalogging.slf4j.Logging
import pl.project13.scala.oculus.hbase.tables.PropsSchema

trait HBaseActions extends Logging {

  val Props = PropsSchema.PropsTable

  def saveDetails() = {


  }
}
