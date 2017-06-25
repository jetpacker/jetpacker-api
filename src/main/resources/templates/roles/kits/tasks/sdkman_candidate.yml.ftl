- set_fact:
    candidate:
      name: "{{ extension.key }}"
      version: "{{ extension.value }}"

- stat: path=~/.sdkman/candidates/{{ candidate.name }}/{{ candidate.version }}
  register: path

- name: install {{ candidate.name }}:{{ candidate.version }}
  command: bash -lc "{{ item }}"
  with_items:
    - sdk install {{ candidate.name }} {{ candidate.version }}
    - mkdir -p /vagrant/.sdk/{{ candidate.name }}
    - cp -R ~/.sdkman/candidates/{{ candidate.name }}/{{ candidate.version }} /vagrant/.sdk/{{ candidate.name }}/{{ candidate.version }}
  when: not path.stat.exists

- name: default to {{ candidate.name }}:{{ candidate.version }}
  command: bash -lc "sdk default {{ candidate.name }} {{ candidate.version }}"