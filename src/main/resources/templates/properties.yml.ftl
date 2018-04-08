timezone: ${machine.timezone}
<#if kits.jdk??>

jdk:
  version: ${kits.jdk.version}
  <#assign extensions = kits.jdk.extensions>
  <#if extensions?? && extensions?size gt 0>
  extensions:
    <#list extensions?keys as name>
    ${name}: ${extensions[name]}
    </#list>
  </#if>
</#if>
<#if kits.node??>

node:
  version: v${kits.node.version}
  nvm_version: v${kits.node.dependencyVersion}
  <#assign extensions = kits.node.extensions>
  <#if extensions?? && extensions?size gt 0>
  extensions:
    <#list extensions?keys as name>
    ${name}: ${extensions[name]}
    </#list>
  </#if>
</#if>
<#if kits.guard??>

guard:
  ruby_version: ${kits.guard.dependencyVersion}
</#if>
<#if containers?? && containers?size gt 0>
  <#list containers?keys as name>

### ${name} ###
${name}:
    <#assign container = containers[name]>
  version: ${container.version}
    <#if container.command??>
  command: ${container.command}
    </#if>
    <#if container.volumesFrom??>
  volumes_from: ${container.volumesFrom}
    </#if>
    <#if container.ports??>
  ports:
      <#list container.ports?keys as key>
    - ${key}:${container.ports[key]}
      </#list>
    </#if>
    <#if container.env??>
  env:
      <#list container.env?keys as key>
    ${key}: ${container.env[key]}
      </#list>
    </#if>
  </#list>
</#if>
<#if dataContainer.volumes?? && dataContainer.volumes?keys?? && dataContainer.volumes?keys?size gt 0>

### ${dataContainer.name} ###
${dataContainer.name}:
  volumes:
  <#list dataContainer.volumes?keys as key>
    - ${key}:${dataContainer.volumes[key]}
  </#list>
</#if>