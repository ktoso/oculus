package pl.project13.scala.oculus.hbase

import org.apache.hadoop.hbase.HBaseConfiguration

trait HBaseConfig {

  implicit val hbaseConf = {
    val c = HBaseConfiguration.create()
    c.set("fs.default.name", "hdfs://10.240.175.101:9000")
    c.set("hbase.rootdir", "hdfs://10.240.175.101:9000/hbase")
    c.set("hbase.cluster.distributed", "true")
    c.set("hbase.zookeeper.quorum", "10.240.175.101")
    c
  }

}
