#
# Cookbook Name::phash
# Recipe:: default


package 'libphash0'
package 'libphash0-dev'
package 'imagemagick'

source_prefix = node['kernel']['machine'] =~ /x86_64/ ? 'phash/64bit/' : 'phash/32bit/'
bin_files = [
  'image_hashes',
  'test_imagephash',
  'test_mhimagehash'
]

bin_files.each do |file|
  cookbook_file "/usr/bin/#{file}" do
    source "#{source_prefix}#{file}"
    mode 0777
  end
end
