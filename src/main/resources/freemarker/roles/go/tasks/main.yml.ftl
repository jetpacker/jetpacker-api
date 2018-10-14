---
- apt: pkg="{{ item }}" state=latest update_cache=yes cache_valid_time=3600
  with_items:
    - bison
  become: yes
  become_method: sudo

- block:
    - stat: path=~/.gvm/bin/gvm
      register: path

    - name: install gvm
      command: bash -lc "bash < <(curl -s -S -L https://raw.githubusercontent.com/moovweb/gvm/master/binscripts/gvm-installer)"
      when: not path.stat.exists
  become: yes
  become_user: vagrant
