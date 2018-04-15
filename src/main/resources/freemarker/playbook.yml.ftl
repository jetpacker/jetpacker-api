---
- hosts: all
  roles:
    - applications
    - commons
    - docker
    - jdk
    - node
    - guard