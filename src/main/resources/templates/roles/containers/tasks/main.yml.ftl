---
### docker-py ###
- apt: pkg=python-pip state=latest update_cache=yes cache_valid_time=3600
- pip: name=docker-py version=1.9.0 umask=0022

### alpine ###
- name: run alpine:latest
  docker_container:
    name: alpine
    image: alpine:{{ alpine.version }}
    volumes: {{ alpine.volumes || default([]) }}
    command: true

- include: docker_container.yml
  with_items: "{{ containers || default([]) }}"
  loop_control:
  loop_var: container