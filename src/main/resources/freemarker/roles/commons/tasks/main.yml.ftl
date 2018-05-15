---
- name: set timezone to {{ timezone }}
  timezone:
    name: "{{ timezone }}"
  become: true
  become_method: sudo

- file: src=/vagrant/workspace dest=~/workspace state=link
  become: true
  become_user: vagrant