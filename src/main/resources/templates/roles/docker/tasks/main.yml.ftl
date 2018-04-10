---
- name: run docker_service
  docker_service:
    project_src: /vagrant/workspace
    recreate: always