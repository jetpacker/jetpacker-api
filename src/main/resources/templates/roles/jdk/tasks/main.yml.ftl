---
### sdkman ###
- stat: path=~/.sdkman/bin/sdkman-init.sh
  register: path

- name: install sdkman
  command: bash -lc "curl -s 'https://get.sdkman.io' | bash"
  when: not path.stat.exists

- name: update sdkman
  command: bash -lc "source ~/.sdkman/bin/sdkman-init.sh && {{ item }}"
  with_items:
    - sdk selfupdate force
    - sdk flush candidates
    - sdk flush broadcast
    - sdk flush archives
    - sdk flush temp
  when: path.stat.exists

- include: sdkman_candidate.yml
  with_dict: "{{ { 'java' : jdk.version } | combine(jdk.extensions | default({})) }}"
  loop_control:
    loop_var: extension