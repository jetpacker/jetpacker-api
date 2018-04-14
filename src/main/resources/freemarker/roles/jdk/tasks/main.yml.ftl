---
- apt: pkg="{{ item }}" state=latest update_cache=yes cache_valid_time=3600
  with_items:
    - zip
    - unzip
  become: true
  become_method: sudo

- block:
    - stat: path=~/.sdkman/bin/sdkman-init.sh
      register: path

    - name: install sdkman
      command: bash -lc "curl -s 'https://get.sdkman.io' | bash"
      when: not path.stat.exists

    - name: update sdkman
      command: bash -lc ". ~/.sdkman/bin/sdkman-init.sh && {{ item }}"
      with_items:
        - sdk selfupdate force
        - sdk flush broadcast
        - sdk flush archives
        - sdk flush temp
        - sdk flush candidates
      when: path.stat.exists

    - include: sdkman_candidate.yml
      with_dict: "{{ { 'java' : jdk.version } | combine(jdk.extensions | default({})) }}"
      loop_control:
        loop_var: extension
  become: true
  become_user: vagrant