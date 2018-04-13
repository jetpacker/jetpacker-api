---
- name: update timezone
  command: dpkg-reconfigure --frontend noninteractive tzdata
  become: true
  become_method: sudo
  listen: update_timezone