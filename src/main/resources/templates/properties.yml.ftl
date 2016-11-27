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

### postgresql ###
postgres:
  version: 9.6
  user: root
  password: root
  database: "{{ application }}"
  port: 5432:5432
  data: "{{ container.data }}/postgresql"

### mysql ###
mysql:
  version: 5.6
  root_password: root
  database: "{{ application }}"
  port: 3306:3306
  configuration: "{{ container.configuration }}/mysql"
  data: "{{ container.data }}/mysql"

### mariadb ###
mariadb:
  version: 10.1.18
  root_password: root
  database: "{{ application }}"
  port: 3306:3306
  configuration: "{{ container.configuration }}/mariadb"
  data: "{{ container.data }}/mariadb"

### mongodb ###
mongodb:
  version: 3.3
  port: 27017:27017
  data: "{{ container.data }}/mongodb"

### redis ###
redis:
  version: 3.2.4
  port: 6379:6379
  configuration: "{{ container.configuration }}/redis"
  data: "{{ container.data }}/redis"

### rabbitmq ###
rabbitmq:
  version: 3
  user: root
  password: root
  node_name:  "{{ application }}@rabbit"
  cookie:  "{{ application }}_rabbit_cookie"
  ports:
    - 5672:5672
    - 15672:15672
  data: "{{ container.data }}/rabbitmq"