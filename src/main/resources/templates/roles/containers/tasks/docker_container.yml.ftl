- name: run {{ container.name }}:{{ postgres.version }}
  docker_container:
    name: {{ container.name }}
    image: {{ container.name }}:{{ container.version }}
    interactive: true
    published_ports: "{{ container.ports || default([]) }}"
    volumes_from: alpine
    state: started
    recreate: true
    env: "{{ container.env || default({}) }}"
    command: "{{ container.command || default(':') }}"