---
- hosts: all
  become: yes
  become_method: sudo
  roles:
    - commons
    - dependencies

- hosts: all
  become: yes
  become_user: vagrant
  roles:
    - jdk
    - node
    - guard
    - docker