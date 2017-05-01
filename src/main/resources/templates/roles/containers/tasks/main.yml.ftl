---
### docker-py ###
- apt: pkg=python-pip state=latest update_cache=yes cache_valid_time=3600
- pip: name=docker-py version=1.9.0 umask=0022

### alpine ###
- name: run alpine:latest
  docker_container:
    name: alpine
    image: alpine:{{ alpine.version }}
    volumes: {{ alpine.volumes }}
    command: true

### postgres ###
- name: run postgres:{{ postgres.version }}
  docker_container:
    name: postgres
    image: postgres:{{ postgres.version }}
    interactive: true
    published_ports: "{{ postgres.ports }}"
    volumes_from: alpine
    state: started
    recreate: true
    env: "{{ postgres.env }}"

### mysql ###
- name: run mysql:{{ mysql.version }}
  docker_container:
    name: mysql
    image: mysql:{{ mysql.version }}
    interactive: true
    published_ports: "{{ mysql.ports }}"
    volumes_from: alpine
    state: started
    recreate: true
    env: "{{ mysql.env }}"

### mariadb ###
- name: run mariadb:{{ mariadb.version }}
  docker_container:
    name: mariadb
    image: mariadb:{{ mariadb.version }}
    interactive: true
    published_ports: "{{ mariadb.ports }}"
    volumes_from: alpine
    state: started
    recreate: true
    env: "{{ mariadb.env }}"

### mongo ###
- name: run mongo:{{ mongo.version }}
  docker_container:
    name: mongo
    image: mongo:{{ mongo.version }}
    interactive: true
    published_ports: "{{ mongo.ports }}"
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
    published_ports: "{{ redis.ports }}"
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
    env: "{{rabbitmq.env}}