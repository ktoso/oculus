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

hbase_version = "0.94.10"
hbase_home_base = "/opt/hbase"
hbase_home = "/opt/hbase-#{hbase_version}"

bash "download hbase" do
  code "wget http://archive.apache.org/dist/hbase/hbase-#{hbase_version}/hbase-#{hbase_version}.tar.gz -O /tmp/hbase.tar.gz --read-timeout=5 --tries=0"
end

bash "extract hbase" do
  code "tar xf /tmp/hbase.tar.gz -C /opt"
end

[
  "hbase-site.xml",
  "regionservers",
  "hbase-env.sh"
].each do |file|
  cookbook_file "#{hbase_home}/conf/#{file}" do
    source "#{file}"
  end
end
