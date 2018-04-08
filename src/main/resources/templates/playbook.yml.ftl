---
- hosts: all
  become: yes
  become_method: sudo
  roles:
    - apt
    - commons
    - docker

- hosts: all
  become: yes
  become_user: vagrant
  roles:
    - jdk
    - node
    - guard
