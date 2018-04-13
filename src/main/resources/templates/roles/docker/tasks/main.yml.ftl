---
- block:
  - apt: pkg=python-pip state=latest update_cache=yes cache_valid_time=3600
  - pip: name="{{ item }}" umask=0022
    with_items:
      - docker-compose
  become: true
  become_method: sudo

- name: run docker_service
  docker_service:
    project_src: /vagrant/workspace
    recreate: always