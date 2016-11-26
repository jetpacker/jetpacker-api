- set_fact:
    candidate_name: "{{ extension.key }}"
    candidate_version: "{{ extension.value }}"

- stat: path=~/.sdkman/candidates/{{ candidate_name }}/{{ candidate_version }}
  register: path

- name: install {{ candidate_name }}:{{ candidate_version }}
  command: bash -lc "{{ item }}"
  with_items:
    - sdk install {{ candidate_name }} {{ candidate_version }}
    - mkdir -p {{ sdk }}/{{ candidate_name }}
    - cp -R ~/.sdkman/candidates/{{ candidate_name }}/{{ candidate_version }} {{ sdk }}/{{ candidate_name }}/{{ candidate_version }}
  when: not path.stat.exists

- name: default to {{ candidate_name }}:{{ candidate_version }}
  command: bash -lc "sdk default {{ candidate_name }} {{ candidate_version }}"