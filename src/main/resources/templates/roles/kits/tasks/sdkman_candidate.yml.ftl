- set_fact:
    candidate_name: "{{ candidate.key }}"
    candidate_version: "{{ candidate.value }}"

- stat: path=~/.sdkman/candidates/{{ candidate_name }}/{{ candidate_version }}
  register: sdkman_candidate

- name: install {{ candidate_name }}:{{ candidate_version }}
  command: bash -lc "{{ item }}"
  with_items:
    - sdk install {{ candidate_name }} {{ candidate_version }}
    - mkdir -p {{ sdk_root }}/{{ candidate_name }}
    - cp -R ~/.sdkman/candidates/{{ candidate_name }}/{{ candidate_version }} {{ sdk_root }}/{{ candidate_name }}/{{ candidate_version }}
  when: not sdkman_candidate.stat.exists

- name: default to {{ candidate_name }}:{{ candidate_version }}
  command: bash -lc "sdk default {{ candidate_name }} {{ candidate_version }}"