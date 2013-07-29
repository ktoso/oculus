http://archive.apache.org/dist/hadoop/core/hadoop-1.0.3/


http://linuxers.org/tutorial/how-extract-images-video-using-ffmpeg

Downloads
---------

For installing on Debian from packages:
http://www.cloudera.com/content/cloudera-content/cloudera-docs/CDH4/4.2.0/CDH4-Installation-Guide/cdh4ig_topic_4_4.html

*Install Java7 on Debian*

```
su -
echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu precise main" | tee -a /etc/apt/sources.list
echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu precise main" | tee -a /etc/apt/sources.list
apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886
apt-get update
apt-get install oracle-java7-installer
exit
```

*Hadoop 1.1.2*

http://archive.apache.org/dist/hadoop/core/hadoop-1.1.2/hadoop-1.1.2.tar.gz

*HBase 0.94.10*

http://ftp.ps.pl/pub/apache/hbase/hbase-0.94.10/hbase-0.94.10.tar.gz

Install tooling
---------------

https://github.com/paulp/sbt-extras

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

Testing Hadoop / HBase Cluster
------------------------------

For testing the cluster Vagrant + Puppet setup prepared here should be used: https://github.com/ktoso/vagrant-cascading-hadoop-cluster

