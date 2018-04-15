---
- name: set timezone
  copy: content={{ timezone }} dest=/etc/timezone mode=0644 backup=yes
  notify: update_timezone
  when: system_timezone != timezone
  become: true
  become_method: sudo

- file: src=/vagrant/workspace dest=~/workspace state=link
  become: true
  become_user: vagrant