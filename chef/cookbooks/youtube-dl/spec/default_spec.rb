require 'chefspec'

describe 'curl::default' do
  let (:chef_run) { ChefSpec::ChefRunner.new.converge 'curl::default' }
  it 'should install curl' do
    chef_run.should install_package 'curl'
  end
end
