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

hadoop_version = "-1.2.1"
hadoop_home = "/opt/hadoop#{hadoop_version}"

[
  "masters", "slaves",
  "core-site.xml", "hdfs-site.xml", "mapred-site.xml",
  "hadoop-env.sh", "hadoop-path.sh"
].each do |file|
  cookbook_file "#{hadoop_home}/conf/#{file}" do
    source "#{file}"
  end
end
