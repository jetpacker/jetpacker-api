---
- apt: pkg="{{ item }}" state=latest update_cache=yes cache_valid_time=3600
  with_items:
    - xz-utils
  become: yes
  become_method: sudo

- block:
    - stat: path=~/.nvm
      register: path

    - name: install nvm:"{{ node.nvm_version }}"
      command: bash -lc "curl -o- https://raw.githubusercontent.com/creationix/nvm/{{ node.nvm_version }}/install.sh | bash"
      when: not path.stat.exists

    - stat: path="~/.nvm/versions/node/{{ node.version }}/bin/node"
      register: path

    - name: "install node:{{ node.version }}"
      command: bash -lc ". ~/.nvm/nvm.sh && nvm install {{ node.version }}"
      when: not path.stat.exists

    - name: "default node:{{ node.version }}"
      command: bash -lc ". ~/.nvm/nvm.sh && nvm alias default {{ node.version }}"
      when: path.stat.exists
  become: yes
  become_user: vagrant

- include: npm_module.yml
  with_dict: "{{ node.extensions | default({}) }}"
  loop_control:
    loop_var: extension

- include: bind_mount_node_modules.yml
  with_items: "{{ node.projects | default([]) }}"
  loop_control:
    loop_var: project
