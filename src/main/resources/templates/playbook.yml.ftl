---
- hosts: all
  roles:
    - applications
    - commons
    - lsyncd
    - docker
    - guard
    - jdk
    - node