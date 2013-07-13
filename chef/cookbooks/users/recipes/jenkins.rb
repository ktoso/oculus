dir = 'ci'
keys = data_bag(dir).map { |id| data_bag_item(dir, id)['key'] }

# define user and group
u = g = 'jenkins'


#enforce jenkins user
group g do
  action :create
end

user u do
  # requires shell for RAD deployment
  gid g
  shell "/bin/bash"
  home "/home/#{u}"
  supports manage_home: true
  action :create
end

# provision ssh key
directory "/home/#{u}/.ssh" do
  owner u
  group u
  mode "0700"
  action :create
end

template "/home/jenkins/.ssh/authorized_keys" do
  source "authorized_keys.erb"
  owner u
  group u
  mode "0600"
  variables :keys => keys
end