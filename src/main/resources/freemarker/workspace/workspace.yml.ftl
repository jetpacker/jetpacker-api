timezone: ${machine.timezone}
<#if kits?? && kits.jdk??>

jdk:
  version: ${kits.jdk.version}
  <#assign extensions = kits.jdk.extensions>
  <#if extensions?? && extensions?size gt 0>
  extensions:
    <#list extensions?keys as name>
    ${name}: ${extensions[name].version}
    </#list>
  </#if>
</#if>
<#if kits?? && kits.node??>

node:
  version: v${kits.node.version}
  nvm_version: v${kits.node.dependencyVersion}
  <#assign extensions = kits.node.extensions>
  <#if extensions?? && extensions?size gt 0>
  extensions:
    <#list extensions?keys as name>
    <#if extensions[name].alias??>'${extensions[name].alias}'<#else>${name}</#if>: ${extensions[name].version}
    </#list>
  </#if>
# projects:
#   - /home/workspace/hello-vue-project
#   - /home/workspace/hello-express-project
</#if>
<#if machine.synchronization?lower_case == "guard">

ruby:
  version: ${kits.ruby.version}
</#if>