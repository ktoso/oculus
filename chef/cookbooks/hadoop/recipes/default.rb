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

# import the cloudera key
cookbook_file "/root/cloudera.key" do
  source "archive.key"
end

# install the key
bash "add cloudera key" do
  code "apt-key add /root/cloudera.key"
end

# Run apt-get update to create the stamp file
template "/etc/apt/sources.list.d/cloudera.list" do
  source "cloudera.list"
  mode "0755"
  notifies :run, resources(:execute => "apt-get update"), :immediately
end
