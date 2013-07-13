keys = data_bag('admins').map { |id| data_bag_item('admins', id)['key'] }

directory "/root/.ssh" do
  owner "root"
  group "root"
  mode 00600
  action :create
end

template "/root/.ssh/authorized_keys" do
  source "authorized_keys.erb"
  owner "root"
  group "root"
  mode "0600"
  variables :keys => keys
end