---
### jdk ###
- apt: pkg="{{ item }}" state=latest update_cache=yes cache_valid_time=3600
  with_items:
    - zip
    - unzip

### sdkman ###
- stat: path=~/.sdkman/bin/sdkman-init.sh
  register: path

- name: install sdkman
  command: bash -lc "{{ item }}"
  with_items:
    - curl -s 'https://get.sdkman.io' | bash
    - source ~/.sdkman/bin/sdkman-init.sh
  when: not path.stat.exists

- name: update sdkman
  command: bash -lc "{{ item }}"
  with_items:
    - sdk selfupdate force
    - sdk flush candidates
    - sdk flush broadcast
    - sdk flush archives
    - sdk flush temp
  when: path.stat.exists

- include: sdkman_candidate.yml
  with_dict: "{{ { 'java' : jdk.version } | combine(jdk.extensions || default({})) }}"
  loop_control:
    loop_var: extension