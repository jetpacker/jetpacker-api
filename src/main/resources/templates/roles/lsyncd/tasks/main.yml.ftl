---
- apt: pkg=lsyncd state=latest update_cache=yes cache_valid_time=3600

- name: create directory
  file: path="{{ item }}" state=directory owner=root group=root mode=0775 recurse=yes
  with_items:
    - /etc/lsyncd
    - "{{ lsyncd.logDir }}"

- name: generate lsyncd configurations
  block:
    - template:
        src: lsyncd.logrotate.j2
        dest: /etc/logrotate.d/lsyncd
        mode: 0644
        owner: root
        group: root

    - template:
        src: lsyncd.conf.j2
        dest: /etc/lsyncd/lsyncd.conf.lua
        owner: root
        group: root
        mode: 0755
      notify: restart_lsyncd