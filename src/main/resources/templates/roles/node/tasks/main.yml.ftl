---
### node ###
- stat: path=~/.nvm
  register: path

- name: install nvm:"{{ node.nvm_version }}"
  command: bash -lc "curl -o- https://raw.githubusercontent.com/creationix/nvm/{{ node.nvm_version }}/install.sh | bash"
  when: not path.stat.exists

- stat: path="~/.nvm/versions/node/{{ node.version }}/bin/node"
  register: path

- name: install node:{{ node.version }}
  command: bash -lc ". ~/.nvm/nvm.sh && {{ item }}"
  with_items:
    - nvm install "{{ node.version }}"
    - nvm alias default "{{ node.version }}"
  when:
    - not path.stat.exists

- include: npm_module.yml
  with_dict: "{{ node.extensions || default({}) }}"
  loop_control:
    loop_var: extension