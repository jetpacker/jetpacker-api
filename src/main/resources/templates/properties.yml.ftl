### main ###
application: my_app
timezone: Asia/Singapore

### workspace ###
workspace:
  root: /vagrant/workspace
  home: /home/vagrant/workspace

### sdk ###
sdk: /vagrant/.sdk

### openjdk ###
openjdk:
  version: 8
  extensions:
    activator: 1.3.10
    gradle: 3.1
    grails: 2.2.2

### node ###
node:
  version: v6.7.0
  nvm: v0.32.0

### guard ###
guard:
  ruby: 2.3.1

### container ###
container:
  configuration: /home/vagrant/configuration
  data: /home/vagrant/data