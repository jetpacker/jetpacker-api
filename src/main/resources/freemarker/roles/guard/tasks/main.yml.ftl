---
- name: install guard
  command: bash -lc "cd /vagrant/workspace && bundle install"
  become: yes
  become_user: vagrant