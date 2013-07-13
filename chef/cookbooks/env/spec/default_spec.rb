require 'chefspec'

describe 'env::default' do
  let (:chef_run) { ChefSpec::ChefRunner.new.converge 'env::default' }
  it 'should do something' do
    pending 'Your recipe examples go here.'
  end
end
