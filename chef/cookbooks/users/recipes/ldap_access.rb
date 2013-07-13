users = data_bag('admins').map { |id| data_bag_item('admins', id) }

ldap_users = users.select { |item| item["ldap-login"] }
ldap_usernames = ldap_users.map { |item| item["ldap-login"] }

ldap_usernames << "@uucp"
excludes = ["ALL"]

template "/etc/security/access.conf" do
  source "access.conf.erb"
  owner "root"
  group "root"
  mode "0600"
  variables ({ :plus => ldap_usernames, :minus => excludes })
end