#
# Cookbook Name:: java
# Recipe:: default
#
# Copyright 2013, YOUR_COMPANY_NAME
#
# All rights reserved - Do Not Redistribute
#
case node['platform']
when "ubuntu", "debian"
  cookbook_file "/root/webupd8team.key" do
    source "keyfile"
  end

  bash "add key" do
    code <<-EOF
      apt-key add /root/webupd8team.key
    EOF
  end

  apt_repository "java-webupd8team" do
	uri "http://ppa.launchpad.net/webupd8team/java/ubuntu"
	distribution node['lsb']['codename']
	components ["main"]
	notifies :run, resources(:execute => "apt-get update"), :immediately
  end
end
