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

key='cdde6d47c54abb46ae808c65dfbee8b8161a3b07'

bash 'add repo' do
  code 'echo deb http://apt.newrelic.com/debian/ newrelic non-free >> /etc/apt/sources.list.d/newrelic.list'
end

bash 'trust repo' do
  code 'wget -O- https://download.newrelic.com/548C16BF.gpg | apt-key add -'
  end

bash 'update apt db' do
  code 'apt-get update'
end

package 'newrelic-sysmond'

bash 'config newrelic' do
  code "nrsysmond-config --set license_key=#{key}"
end

bash 'start monitoring daemon' do
  code '/etc/init.d/newrelic-sysmond start'
end
