# -*- mode: ruby -*-
# vi: set ft=ruby :

required_plugins = %w(vagrant-vbguest)

plugins_to_install = required_plugins.select { |plugin| not Vagrant.has_plugin? plugin }
if not plugins_to_install.empty?
  puts "Installing plugins: ${"#"}{plugins_to_install.join(' ')}"
  if system "vagrant plugin install ${"#"}{plugins_to_install.join(' ')}"
    exec "vagrant ${"#"}{ARGV.join(' ')}"
  else
    abort "Installation of one or more plugins has failed. Aborting."
  end
end

Vagrant.configure("2") do |config|
  config.vm.box = '${machine.box}'

  config.vm.provider "virtualbox" do |v|
    v.memory = ${machine.memory?c}
    v.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
    v.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
  end

  # forwarded ports
  # config.vm.network 'forwarded_port', guest: 8080, host: 8080, auto_correct: true

  config.vm.provision "docker"

  config.vm.provision 'ansible', run: 'always', type: 'ansible_local' do |a|
    a.become = true
    a.verbose = '-vvv'
    a.playbook = './playbook.yml'
    a.extra_vars = './extra_vars.yml'
  end
end