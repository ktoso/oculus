# used by most Hadoop tools even though bin/hadoop warns against it
export HADOOP_HOME_WARN_SUPPRESS="true"
export HADOOP_HOME=/opt/hadoop-1.2.1
export HADOOP_CONF_DIR=$HADOOP_HOME/conf
export PATH=$HADOOP_HOME/bin:$PATH