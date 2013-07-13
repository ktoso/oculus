package "ntp"

cookbook_file "/etc/timezone" do
  source "timezone"
  mode 0644
end
