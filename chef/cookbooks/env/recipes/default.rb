cookbook_file '/etc/profile.d/utf_env.sh' do
  source 'utf_env.sh'
  mode '0644'
end