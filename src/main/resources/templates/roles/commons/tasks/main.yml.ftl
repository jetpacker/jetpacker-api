---
### git ###
- apt: pkg=git state=latest update_cache=yes cache_valid_time=3600

### timezone ###
- name: set timezone
  copy: content={{ timezone }} dest=/etc/timezone mode=0644 backup=yes
  notify: update timezone
  when: system_timezone != timezone

### symbolic links ###
- file: src=/vagrant/workspace dest=/home/vagrant/workspace state=link