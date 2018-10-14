---
- set_fact:
    candidate:
      name: "{{ extension.key }}"
      version: "{{ extension.value }}"

- block:
    - stat: path=~/.sdkman/candidates/{{ candidate.name }}/{{ candidate.version }}
      register: path

    - name: install {{ candidate.name }}:{{ candidate.version }}
      command: bash -lc ". ~/.sdkman/bin/sdkman-init.sh && sdk install {{ candidate.name }} {{ candidate.version }}"
      when: not path.stat.exists

    - name: default to {{ candidate.name }}:{{ candidate.version }}
      command: bash -lc ". ~/.sdkman/bin/sdkman-init.sh && sdk default {{ candidate.name }} {{ candidate.version }}"
      when: path.stat.exists
  become: yes
  become_user: vagrant
