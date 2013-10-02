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

bash 'download grrr' do
  code "wget --no-check-certificate http://raw.github.com/fs111/grrrr/master/grrr -O /tmp/grrr && chmod +x /tmp/grrr"
end

bash 'download hadoop' do
  code "/tmp/grrr /hadoop/common/hadoop-1.2.1/hadoop-1.2.1.tar.gz -O /tmp/hadoop.tar.gz --read-timeout=5 --tries=0"
end

bash 'unpack hadoop' do
  code "mkdir -p /opt && tar xf /tmp/hadoop.tar.gz -C /opt"
end

bash "initialise and start hdfs / namenode" do
  #code "sudo -u hdfs hdfs namenode -format"
  #
  #notifies :start, "service[hadoop-hdfs-namenode]", :immediately
end
