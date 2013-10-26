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

hue_version = "2.5.0"
hue_home_base = "/opt/hue"
hue_home = "/opt/hue-#{hue_version}"

bash "download hue" do
  code "wget https://dl.dropboxusercontent.com/u/730827/hue/releases/#{hue_version}/hue-#{hue_version}.tgz -O /tmp/hue.tar.gz"
end

bash "extract hue" do
  code "tar xf /tmp/hue.tar.gz -C /opt"
end

make_dependencies = [ "ant", "gcc", "g++", "libkrb5-dev", "libmysqlclient-dev",
  "libssl-dev", "libsasl2-dev", "libsasl2-modules-gssapi-mit", "libsqlite3-dev",
  "libxml2-dev", "libxslt-dev", "maven2", "libldap2-dev", "python-dev", "python-simplejson",
  "python-setuptools"
]

make_dependencies.each do |pack|
  package pack
end

bash "install and run hue" do
  cwd "/opt"
  code <<-EOS
    PREFIX=/opt/hue make install

    # start
    ${PREFIX}/hue/build/env/bin/supervisor

    # install hadoop plugin
    cd /opt/hadoop-1.2.1/lib
    ln -fs ${PREFIX}/hue/desktop/libs/hadoop/java-lib/hue*jar
  EOS
end

bash "start hue" do
  code <<-EOS
    export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
    export HIVE_HOME=/opt/hive

    cd /usr/bin && ln -fs /opt/hadoop-1.2.1/bin/hadoop
    cd /etc
    mkdir -p /hadoop
    cd hadoop && ln -fs /opt/hadoop-1.2.1/conf
    cd /usr/bin && ln -fs /opt/hadoop-1.2.1/bin/conf/hadoop-env.sh hadoop-config.sh



    cd /opt/hue-2.5.0/hue && sudo huebuild/env/bin/supervisor

  EOS
end

[
  "hue-site.xml",
  "regionservers",
  "hue-env.sh"
].each do |file|
  cookbook_file "#{hue_home}/conf/#{file}" do
    source "#{file}"
  end
end
