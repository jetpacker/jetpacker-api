---
- block:
    - stat: path=~/.rvm
      register: path

    - name: install ruby
      command: bash -lc "{{ item }}"
      with_items:
        - gpg --keyserver hkp://keys.gnupg.net --recv-keys 409B6B1796C275462A1703113804BB82D39DC0E3 7D2BAF1CF37B13E2069D6956105BD0E739499BDB
        - curl -sSL https://get.rvm.io | bash -s stable
        - rvm install {{ ruby.version }}
        - rvm --default use {{ ruby.version }}
        - gem install bundler
      when:
        - not path.stat.exists
  become: true
  become_user: vagrant