# -*- mode: ruby -*-
# vi: set ft=ruby :

required_plugins = %w(
  vagrant-vbguest
<#if machine.synchronization?lower_case == "rsync">
  vagrant-gatling-rsync
</#if>
)

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
<#if machine.synchronization?lower_case == "rsync">

  config.vm.synced_folder ".", "/vagrant", type: "rsync", rsync__exclude: [ ".git/", ".idea/", "node_modules/" ]

  config.gatling.latency = 2.5
  config.gatling.time_format = "%H:%M:%S"
  config.gatling.rsync_on_startup = false
</#if>

  ### http/https ###
  # config.vm.network 'forwarded_port', guest: 8080, host: 8080, auto_correct: false
  # config.vm.network 'forwarded_port', guest: 8443, host: 8443, auto_correct: false
<#if containers??>
  <#list containers?keys as name>
    <#assign container = containers[name]>

  ### ${name} ###
    <#list container.ports?keys as key>
  # config.vm.network 'forwarded_port', guest: ${container.ports[key]}, host: ${key}, auto_correct: false
    </#list>
  </#list>

  config.vm.provision "docker"
</#if>

  config.vm.provision 'ansible', run: 'always', type: 'ansible_local' do |a|
    a.become = true
    a.verbose = '-vvv'
    a.playbook = './playbook.yml'
    a.extra_vars = './workspace/workspace.yml'
  end
end