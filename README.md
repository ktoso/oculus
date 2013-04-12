hadoop 1.0.3

http://www.michael-noll.com/tutorials/running-hadoop-on-ubuntu-linux-single-node-cluster/#run-the-mapreduce-job

http://archive.apache.org/dist/hadoop/core/hadoop-1.0.3/


http://linuxers.org/tutorial/how-extract-images-video-using-ffmpeg

Downloads
---------

*Hadoop 1.0.3*

http://archive.apache.org/dist/hadoop/core/hadoop-1.0.3/hadoop-1.0.3.tar.gz

*HBase 0.94.6*

http://ftp.ps.pl/pub/apache/hbase/hbase-0.94.6/hbase-0.94.6.tar.gz

Running the app
---------------
required: scala, simple build tool

for downloading movies:
```
sbt
project downloader
run
```

for computing metrics from movies on HDFS
```
sbt
project scalding
run pl.project13.scala.oculus.job.VideoToPicturesJob --hdfs --output hdfs://oculus-one.project13.pl:54310/oculus/target/Pst9a8tZbkk.webm.img-size.tsv --input hdfs://oculus-one.project13.pl:54310/oculus/source/Pst9a8tZbkk.webm.seq
```
