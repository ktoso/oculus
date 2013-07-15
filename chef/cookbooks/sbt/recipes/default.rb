#
# Cookbook Name:: curl
# Recipe:: default
#
# Copyright 2013, YOUR_COMPANY_NAME
#
# All rights reserved - Do Not Redistribute


bash "install sbt for debian" do
  user "root"
  cwd "/tmp"
  code <<-EOH
    wget http://scalasbt.artifactoryonline.com/scalasbt/sbt-native-packages/org/scala-sbt/sbt//0.12.4/sbt.deb
    dpkg -i sbt.deb
    rm sbt.deb
  EOH
end
