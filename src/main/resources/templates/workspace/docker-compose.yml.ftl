version: "3"
<#if containers?? && containers?size gt 0>

services:
  <#list containers?keys as name>
  ${name}:
    <#assign container = containers[name]>
    container_name: ${name}
    image: ${name}:${container.version}
    <#if container.ports?? && container.ports?size gt 0>
    ports:
      <#list container.ports?keys as key>
      - ${key}:${container.ports[key]}
      </#list>
    </#if>
    <#if container.environment?? && container.environment?size gt 0>
    environment:
      <#list container.environment?keys as key>
      - ${key}=${container.environment[key]}
      </#list>
    </#if>
    <#if container.volumes?? && container.volumes?size gt 0>
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
<#if namedVolumes?? && namedVolumes?size gt 0>
volumes:
  <#list namedVolumes as namedVolume>
  ${namedVolume}:
  </#list>
</#if>