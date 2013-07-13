require 'chefspec'

describe 'links::default' do
  let (:chef_run) { ChefSpec::ChefRunner.new.converge 'links::default' }
  it 'should install links' do
    chef_run.should install_package 'links'
  end
end
