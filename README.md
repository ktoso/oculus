http://archive.apache.org/dist/hadoop/core/hadoop-1.0.3/


http://linuxers.org/tutorial/how-extract-images-video-using-ffmpeg

Downloads
---------

For installing on Debian from packages:
http://www.cloudera.com/content/cloudera-content/cloudera-docs/CDH4/4.2.0/CDH4-Installation-Guide/cdh4ig_topic_4_4.html

Monitoring urls
---------------

hadoop

```
http://192.168.7.10:50030/jobtracker.jsp
```

hbase

```
http://192.168.7.10:60010/master-status
```



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

Create stuff in hbase
=====================

```
hbase(main):002:0> create 'frames', {NAME => 'youtube'}
scan 'frames'
0 row(s) in 8.2020 seconds


hbase(main):003:0> scan 'frames'
ROW                                                          COLUMN+CELL
0 row(s) in 0.2590 seconds


hbase(main):005:0> put 'frames', 'asdf', 'youtube:id', 'asdf'
0 row(s) in 0.1150 seconds

hbase(main):006:0> put 'frames', 'asdf', 'youtube:meta',  "{metadata: jsonstuff}"
0 row(s) in 0.0180 seconds


hbase(main):007:0> scan 'frames'
ROW                                                          COLUMN+CELL
 asdf                                                        column=youtube:id, timestamp=1375312436843, value=asdf
 asdf                                                        column=youtube:meta, timestamp=1375312452591, value={metadata: jsonstuff}
```

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

