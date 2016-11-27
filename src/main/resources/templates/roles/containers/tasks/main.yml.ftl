---
### docker-py ###
- apt: pkg=python-pip state=latest update_cache=yes cache_valid_time=3600
- pip: name=docker-py version=1.9.0 umask=0022

### alpine ###
- name: run alpine:latest
  docker_container:
    name: alpine
    image: alpine:latest
    volumes:
      - "{{ postgres.data }}:/var/lib/postgresql/data/pgdata"

      - "{{ mysql.configuration }}:/etc/mysql/conf.d"
      - "{{ mysql.data }}:/var/lib/mysql"

      # - "{{ mariadb.configuration }}:/etc/mysql/conf.d"
      # - "{{ mariadb.data }}:/var/lib/mysql"

      - "{{ mongodb.data }}:/data/db"

      - "{{ redis.configuration }}:/usr/local/etc/redis"
      - "{{ redis.data }}:/data"

      - "{{ rabbitmq.data }}:/var/lib/rabbitmq"
    command: "true"

### postgresql ###
- name: run postgres:{{ postgres.version }}
  docker_container:
    name: postgresql
    image: postgres:{{ postgres.version }}
    interactive: true
    published_ports: "{{ postgres.port }}"
    volumes_from: alpine
    state: started
    recreate: true
    env:
      POSTGRES_USER: "{{ postgres.user }}"
      POSTGRES_PASSWORD: "{{ postgres.password }}"
      POSTGRES_DB: "{{ postgres.database }}"
      PGDATA : /var/lib/postgresql/data/pgdata

### mysql ###
# - name: run mysql:{{ mysql.version }}
#   docker_container:
#     name: mysql
#     image: mysql:{{ mysql.version }}
#     interactive: true
#     published_ports: "{{ mysql.port }}"
#     volumes_from: alpine
#     state: started
#     recreate: true
#     env:
#       MYSQL_ROOT_PASSWORD: "{{ mysql.root_password }}"
#       MYSQL_DATABASE: "{{ mysql.database }}"

### mariadb ###
- name: run mariadb:{{ mariadb.version }}
  docker_container:
    name: mariadb
    image: mariadb:{{ mariadb.version }}
    interactive: true
    published_ports: "{{ mariadb.port }}"
    volumes_from: alpine
    state: started
    recreate: true
    env:
      MYSQL_ROOT_PASSWORD: "{{ mariadb.root_password }}"
      MYSQL_DATABASE: "{{ mariadb.database }}"

### mongodb ###
- name: run mongodb:{{ mongodb.version }}
  docker_container:
    name: mongodb
    image: mongo:{{ mongodb.version }}
    interactive: true
    published_ports: "{{ mongodb.port }}"
    volumes_from: alpine
    state: started
    recreate: true

### redis ###
- name: run redis:{{ redis.version }}
  docker_container:
    name: redis
    image: redis:{{ redis.version }}
    command: redis-server --appendonly yes
    interactive: true
    published_ports: "{{ redis.port }}"
    volumes_from: alpine
    state: started
    recreate: true

### rabbitmq ###
- name: run rabbitmq:{{ rabbitmq.version }}
  docker_container:
    name: rabbitmq
    image: rabbitmq:{{ rabbitmq.version }}-management
    interactive: true
    published_ports: "{{ rabbitmq.ports }}"
    volumes_from: alpine
    state: started
    recreate: true
    env:
      RABBITMQ_DEFAULT_USER: "{{ rabbitmq.user }}"
      RABBITMQ_DEFAULT_PASS: "{{ rabbitmq.password }}"
      RABBITMQ_NODE_NAME: "{{ rabbitmq.node_name }}"
      RABBITMQ_ERLANG_COOKIE: "{{ rabbitmq.cookie }}"