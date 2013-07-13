require 'chefspec'

describe 'sysstat::default' do
  let (:chef_run) { ChefSpec::ChefRunner.new.converge 'sysstat::default' }
  it 'should install sysstat' do
    chef_run.should install_package 'sysstat'
  end
end
