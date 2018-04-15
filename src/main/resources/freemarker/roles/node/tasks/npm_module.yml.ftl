---
- set_fact:
    module:
      name: "{{ extension.key }}"
      version: "{{ extension.value }}"

- block:
    - stat: path=~/.nvm/versions/node/{{ node.version }}/lib/node_modules/{{ module.name }}
      register: path

    - name: install {{ module.name }}:{{ module.version }}
      command: bash -lc ". ~/.nvm/nvm.sh && {{ item }}"
      with_items:
        - npm install -g {{ module.name }}@{{ module.version }}
      when: not path.stat.exists
  become: true
  become_user: vagrant