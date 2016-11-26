---
## openjdk
- apt_repository: repo=ppa:openjdk-r/ppa update_cache=yes

- apt: pkg="{{ item }}" state=latest update_cache=yes cache_valid_time=3600
  with_items:
    - openjdk-{{ openjdk.version }}-jdk
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
  with_dict: "{{ openjdk.extensions }}"
  loop_control:
    loop_var: extension

### node ###
- stat: path=~/.nvm
  register: path

- name: install nvm:{{ node.nvm }}
  command: bash -lc "curl -o- https://raw.githubusercontent.com/creationix/nvm/{{ node.nvm }}/install.sh | bash"
  when: not path.stat.exists

- stat: path=~/.nvm/versions/node/{{ node.version }}/bin/node
  register: path

- name: install node:{{ node.version }}
  command: bash -lc ". ~/.nvm/nvm.sh && {{ item }}"
  with_items:
    - nvm install "{{ node.version }}"
    - nvm alias default "{{ node.version }}"
  when:
    - not path.stat.exists

### guard ###
- stat: path=~/.rvm
  register: path

- name: install guard
  command: bash -lc "{{ item }}"
  with_items:
    - gpg --keyserver hkp://keys.gnupg.net --recv-keys 409B6B1796C275462A1703113804BB82D39DC0E3
    - curl -sSL https://get.rvm.io | bash -s stable
    - rvm install {{ guard.ruby }}
    - rvm --default use {{ guard.ruby }}
    - gem install bundler
    - cd {{ workspace.root }} && bundle install
  when:
    - not path.stat.exists