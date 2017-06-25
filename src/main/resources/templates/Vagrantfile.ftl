# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  config.vm.box = '${machine.box}'

  config.vm.provider "virtualbox" do |virtualbox|
    virtualbox.memory = ${machine.memory?c}
  end

  # forwarded ports
  # config.vm.network 'forwarded_port', guest: 8080, host: 8080, auto_correct: true

  config.vm.provision "docker"

  config.vm.provision 'ansible', run: 'always', type: 'ansible_local' do |ansible|
    ansible.sudo = true
    ansible.verbose = true
    ansible.playbook = './playbook.yml'
    ansible.extra_vars = './properties.yml'
  end
end