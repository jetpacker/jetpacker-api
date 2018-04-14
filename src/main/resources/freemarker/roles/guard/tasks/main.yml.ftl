---
- stat: path=~/.rvm
  register: path

- name: install guard
  command: bash -lc "{{ item }}"
  with_items:
    - gpg --keyserver hkp://keys.gnupg.net --recv-keys 409B6B1796C275462A1703113804BB82D39DC0E3
    - curl -sSL https://get.rvm.io | bash -s stable
    - rvm install {{ guard.ruby_version }}
    - rvm --default use {{ guard.ruby_version }}
    - gem install bundler
    - cd /vagrant/workspace && bundle install
  when:
    - not path.stat.exists