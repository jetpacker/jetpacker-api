### main ###
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
  dependency: v0.32.0

### guard ###
guard:
  dependency: 2.3.1

### postgresql ###
postgres:
  version: 9.6
  env:
    POSTGRES_USER: root
    POSTGRES_PASSWORD: root
    POSTGRES_DB: my_database
    PGDATA: /var/lib/postgresql/data/pgdata
  ports:
    - 5432:5432
  volumes:
  - /home/vagrant/configuration/postgres:/var/lib/postgresql/data/pgdata

### mysql ###
mysql:
  version: 5.6
  env:
    MYSQL_ROOT_PASSWORD: root
    MYSQL_DATABASE: my_database
  ports:
    - 3306:3306
  volumes:
    - /home/vagrant/configuration/mysql:/etc/mysql/conf.d
    - /home/vagrant/data/mysql:/var/lib/mysql

### mariadb ###
mariadb:
  version: 10.1.18
  env:
    MYSQL_ROOT_PASSWORD: root
    MYSQL_DATABASE: my_database
  ports:
    - 3306:3306
  volumes:
    - /home/vagrant/docker/configuration/mariadb:/etc/mysql/conf.d
    - /home/vagrant/docker/data/mariadb:/var/lib/mysql

### mongodb ###
mongodb:
  version: 3.3
  ports:
    - 27017:27017
  volumes:
    - /home/vagrant/docker/data/mongo:/data/db

### redis ###
redis:
  version: 3.2.4
  ports:
    - 6379:6379
  volumes:
    - /home/vagrant/docker/configuration/redis:/usr/local/etc/redis
    - /home/vagrant/docker/data/redis:/data

### rabbitmq ###
rabbitmq:
  version: 3
  env:
    RABBITMQ_DEFAULT_USER: root
    RABBITMQ_DEFAULT_PASS: root
    RABBITMQ_NODE_NAME: my_node@rabbit
    RABBITMQ_ERLANG_COOKIE: my_rabbit_cookie
  ports:
    - 5672:5672
  volumes:
    - /home/vagrant/docker/data/rabbitmq:/var/lib/rabbitmq