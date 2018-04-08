- set_fact:
    module:
      name: "{{ extension.key }}"
      version: "{{ extension.value }}"

- name: install {{ module.name }}:{{ module.version }}
  command: bash -lc ". ~/.nvm/nvm.sh && npm install -g {{ module.name }}@{{ module.version }}"