include_recipe "java"

execute "mark Oracle license as accepted" do
  
  command <<-EOF
    echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
    EOF
end

if node['platform'] == 'ubuntu'
  package "oracle-java7-installer"
elsif node['platform'] == 'debian'
  package "openjdk-7-jre"
  package "openjdk-7-jdk"
end