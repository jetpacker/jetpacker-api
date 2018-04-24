---
- name: install guard
  command: bash -lc "cd /vagrant/workspace && bundle install"
  become: true
  become_user: vagrant