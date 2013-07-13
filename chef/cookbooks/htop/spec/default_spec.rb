require 'chefspec'

describe 'htop::default' do
  let (:chef_run) { ChefSpec::ChefRunner.new.converge 'htop::default' }
  it 'should install htop' do
    chef_run.should install_package 'htop'
  end
end
