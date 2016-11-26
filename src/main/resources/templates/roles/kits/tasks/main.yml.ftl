---
### sdkman ###
- stat: path=~/.sdkman/bin/sdkman-init.sh
  register: sdkman

- name: install sdkman
  command: bash -lc "{{ item }}"
  with_items:
    - curl -s 'https://get.sdkman.io' | bash
    - source ~/.sdkman/bin/sdkman-init.sh
  when: not sdkman.stat.exists

- name: update sdkman
  command: bash -lc "{{ item }}"
  with_items:
    - sdk selfupdate force
    - sdk flush candidates
    - sdk flush broadcast
    - sdk flush archives
    - sdk flush temp
  when: sdkman.stat.exists

- include: sdkman_candidate.yml
  with_dict: "{{ sdkman_candidates }}"
  loop_control:
    loop_var: candidate

### node ###
- stat: path=~/.nvm
  register: nvm

- name: install nvm:{{ nvm }}
  command: bash -lc "curl -o- https://raw.githubusercontent.com/creationix/nvm/{{ nvm }}/install.sh | bash"
  when: not nvm.stat.exists

- stat: path=~/.nvm/versions/node/{{ node }}/bin/node
  register: node

- name: install node:{{ node }}
  command: bash -lc ". ~/.nvm/nvm.sh && {{ item }}"
  with_items:
    - nvm install {{ node }}
    - nvm alias default {{ node }}
  when:
    - not node.stat.exists

### guard ###
- stat: path=~/.rvm
  register: rvm

- name: install guard
  command: bash -lc "{{ item }}"
  with_items:
    - gpg --keyserver hkp://keys.gnupg.net --recv-keys 409B6B1796C275462A1703113804BB82D39DC0E3
    - curl -sSL https://get.rvm.io | bash -s stable
    - rvm install {{ ruby }}
    - rvm --default use {{ ruby }}
    - gem install bundler
    - cd {{ workspace_root }} && bundle install
  when: 
    - not rvm.stat.exists