name              "hbase"
maintainer        "Konrad Malawski"
maintainer_email  "konrad.malawski@project13.pl"
license           "Apache 2.0"
description       "Configures apt and apt services and LWRPs for managing apt repositories and preferences"
long_description  IO.read(File.join(File.dirname(__FILE__), 'README.md'))
version           "2.0.1"
depends           "hadoop"

%w{ ubuntu debian }.each do |os|
  supports os
end
