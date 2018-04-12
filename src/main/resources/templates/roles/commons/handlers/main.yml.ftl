---
- name: update timezone
  command: dpkg-reconfigure --frontend noninteractive tzdata
  listen: update_timezone