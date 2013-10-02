#
# Cookbook Name:: curl
# Recipe:: default
#
# Copyright 2013, YOUR_COMPANY_NAME
#
# All rights reserved - Do Not Redistribute

bash "download youtube-dl" do
  code "curl https://yt-dl.org/downloads/2013.10.01.1/youtube-dl -o /usr/local/bin/youtube-dl"
end

bash "install youtube-dl" do
  code "chmod a+x /usr/local/bin/youtube-dl"
end