version: "3"
<#if containers?? && containers?size gt 0>

services:
  <#list containers?keys as name>
  ${name}:
    <#assign container = containers[name]>
    container_name: ${name}
    image: ${name}:${container.version}
    <#if container.ports??>
    ports:
      <#list container.ports?keys as key>
      - ${key}:${container.ports[key]}
      </#list>
    </#if>
    <#if container.environment??>
    environment:
      <#list container.environment?keys as key>
      - ${key}=${container.environment[key]}
      </#list>
    </#if>
    <#if container.volumes??>
    volumes:
      <#list container.volumes as volume>
      - ${volume}
      </#list>
    </#if>
    <#if container.command??>
    command: ${container.command}
    </#if>
  
  </#list>
</#if>
<#if volumes?? && volumes?size gt 0>
volumes:
  <#list volumes as volume>
  ${volume}:
  </#list>
</#if>