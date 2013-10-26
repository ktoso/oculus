hive_version = "0.12.0"
hive_home = "/opt/hive-#{hive_version}"

bash "download hive" do
  code "wget http://mirror.catn.com/pub/apache/hive/hive-#{hive_version}/hive-#{hive_version}-bin.tar.gz -O /tmp/hive.tar.gz --read-timeout=5 --tries=0"
end

bash "extract hive" do
  code "tar xf /tmp/hive.tar.gz -C /opt && ln -fs /opt/hive-0.12.0-bin /opt/hive"
end

bash "create hive scratchdir" do
  code "mkdir -p /tmp/hive"
end

[
  "hive-site.xml",
  "hive-env.sh"
].each do |file|
  cookbook_file "#{hive_home}/conf/#{file}" do
    source "#{file}"
  end
end
