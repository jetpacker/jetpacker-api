---
### docker-py ###
- apt: pkg=python-pip state=latest update_cache=yes cache_valid_time=3600
- pip: name=docker-py version=1.9.0 umask=0022
<#if dataContainer.volumes?keys?? && dataContainer.volumes?keys?size gt 0>

### alpine ###
- name: run ${dataContainer.name}:latest
  docker_container:
    name: alpine
    image: ${dataContainer.name}:latest
    volumes: {{ ${dataContainer.name}.volumes || default([]) }}
    recreate: true
</#if>
<#if containers?? && containers?size gt 0>
  <#list containers?keys as name>

- name: run ${name}:"{{ ${name}.version }}"
  docker_container:
    name: ${name}
    image: "${name}:{{ ${name}.version }}"
    interactive: true
    <#assign container = containers[name]>
    <#if container.ports??>
    published_ports: "{{ ${name}.ports || default([]) }}"
    </#if>
    <#if container.volumesFrom??>
    volumes_from: "{{ ${name}.volumes_from }}"
    </#if>
    state: started
    recreate: true
    <#if container.env??>
    env: "{{ ${name}.env }}"
    </#if>
    <#if container.command??>
    command: "{{ ${name}.command }}"
    </#if>
  </#list>
</#if>