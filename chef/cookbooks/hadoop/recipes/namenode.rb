#
# Cookbook Name:: apt
# Recipe:: default
#
# Copyright 2008-2011, Opscode, Inc.
# Copyright 2009, Bryan McLellan <btm@loftninjas.org>
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

package 'hadoop-hdfs-namenode'

# install all the things, pseudo distributed mode
package 'hadoop-conf-pseudo'

service "hadoop-hdfs-namenode" do
  supports :start => true, :stop => true, :restart => true
end

# configure hdfs
["core-site.xml", "hdfs-site.xml", "yarn-site.xml"].each do |file|
  template "/etc/hadoop/conf/#{file}" do
    source "conf.pseudo/#{file}.erb"
    variables :namenode_ip => "192.168.22.21"
    mode "0755"
  end
end

# initialise and start the namenode
bash "initialise and start hdfs / namenode" do
  code "sudo -u hdfs hdfs namenode -format"

  notifies :start, "service[hadoop-hdfs-namenode]", :immediately
end
