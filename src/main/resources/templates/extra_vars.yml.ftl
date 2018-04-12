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

lsyncd:
  logDir: /tmp/lsyncd
  logFile: lsyncd.log
  statusFile: lsyncd.status
  syncs:
    - source: /vagrant/workspace
      target: /home/vagrant/workspace
      excludes:
        - node_module
        - target