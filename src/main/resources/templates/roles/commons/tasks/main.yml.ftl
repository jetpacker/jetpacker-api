---
- apt: pkg="{{ item }}" state=latest update_cache=yes cache_valid_time=3600
  with_items:
    - git
    - python-pip

- pip: name=docker-py version=1.9.0 umask=0022

- name: set timezone
  copy: content={{ timezone }} dest=/etc/timezone mode=0644 backup=yes
  notify: update timezone
  when: system_timezone != timezone

- file: src={{ workspace.root }} dest={{ workspace.home }} state=link