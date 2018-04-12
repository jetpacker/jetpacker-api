---
- name: set timezone
  copy: content={{ timezone }} dest=/etc/timezone mode=0644 backup=yes
  notify: update_timezone
  when: system_timezone != timezone

- file: src=/vagrant/workspace dest=/home/vagrant/workspace state=link