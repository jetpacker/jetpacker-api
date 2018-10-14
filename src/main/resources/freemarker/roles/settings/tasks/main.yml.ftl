---
- name: set timezone to {{ timezone }}
  timezone:
    name: "{{ timezone }}"
  become: yes
  become_method: sudo

- file: src=/vagrant/workspace dest=~/workspace state=link
  become: yes
  become_user: vagrant