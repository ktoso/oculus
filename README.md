Disclaimer
==========

This project is pretty chaotically developed, as this is the easies flow for me.

*None of this code is meant for "general consumption".*

Links
-----

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
http://10.240.57.179:50030/jobtracker.jsp
```

hbase

```
http://10.240.57.179:60010/master-status
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

Run downloader
--------------

```
```

List files on HDFS from the downlader
-------------------------------------

```
$ hadoop fs -ls /oculus/source
Found 6 items
-rw-r--r--   3 kmalawski supergroup 1161390621 2013-08-01 00:32 /oculus/source/-A1e_vS5gn4.webm.seq
-rw-r--r--   3 kmalawski supergroup   78396304 2013-08-01 00:45 /oculus/source/0a78kzAffb4.webm.seq
-rw-r--r--   3 kmalawski supergroup 3319289461 2013-08-01 00:36 /oculus/source/G8dMlGq6CBE.webm.seq
-rw-r--r--   3 kmalawski supergroup 1852768320 2013-08-01 00:25 /oculus/source/cj2uhyfVNmQ.webm.seq
-rw-r--r--   3 kmalawski supergroup 5868144017 2013-08-01 00:28 /oculus/source/fPAaYvL5Vpw.webm.seq
-rw-r--r--   3 kmalawski supergroup  128484760 2013-08-01 00:34 /oculus/source/gVi_2lHBVhQ.webm.seq
```

Calculate the PHASH function (reqs hbase)
-----------------------------------------

```
sbt shell

> project scalding

> run pl.project13.scala.oculus.job.VideoToPicturesJob --hdfs --input hdfs://10.240.57.179:9000/oculus/source/cj2uhyfVNmQ.webm --output ignore.out
```

Run sanity test - word count
============================
```
sbt shell
> project scalding
> run pl.project13.scala.oculus.job.WordCountJob --hdfs --input hdfs://10.240.57.179:9000/demotext.txt --output hdfs://10.240.57.179:9000/wordcount.out
```

Remember how to get results:

```
hadoop fs -getmerge /ignore.out ignore.out
```


compile phash test runner
-------------------------
Examples are in: `/usr/share/doc/libphash0-dev/examples`

```
 g++ -g -O3 -I. -pthread -I /usr/local/include image_hashes.cpp -L/usr/local/lib -lpHash -o imageHashes
```

Compute mh_hash and cdt_hash on a given image:

```
vagrant@vagrant-ubuntu-precise-32:/tmp$

./image_hashes  compr/jasper_johns.jpg

mh_hash: 86 15 7 203 128 57 28 14 7 177 209 106 140 33 192 224 115 100 15 87 96 225 126 7 35 96 108 241 142 7 31 0 56 29 44 109 31 171 255 31 143 199 227 96 45 3 206 48 252 126 63 31 13 45 224 122 56 159 142 50 227 101 100 60 40 243 30 14 1 31 128 104
dct_hash: 10510202026326186564
```

Use these values to build keys in your db.

Differences between hashes are calculated as Hamming Distance ( http://en.wikipedia.org/wiki/Hamming_distance ).



Run the phash job
-----------------
This calculates phashes of all seq files (well, here just one):

```
run pl.project13.scala.oculus.job.HashHistVideoSeqFilesJob --hdfs --input hdfs://10.240.57.179:9000/oculus/source/0a78kzAffb4.webm.seq --output hdfs://10.240.57.179:9000/ignore.out
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


phash on ubuntu
---------------
it's packahged:

```
sudo apt-get install libphash0-dev libphash0
```
