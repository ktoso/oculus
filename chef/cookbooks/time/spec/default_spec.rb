require 'chhttps://github.com/opscode-cookbooks/apt.gitefspec'

describe 'time::default' do
  let (:chef_run) { ChefSpec::ChefRunner.new.converge 'time::default' }
  it 'should install ntp' do
    chef_run.should install_package 'ntp'
  end
end
