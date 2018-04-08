---
- hosts: all
  roles:
    - commons
    - docker

- hosts: all
  become_user: vagrant
  roles:
    -