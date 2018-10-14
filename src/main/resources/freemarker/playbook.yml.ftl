---
- hosts: all
  roles:
    - apps
    - commons
<#if containers?? && containers?size gt 0>
    - docker
</#if>
<#if kits??>
    <#if kits.jdk??>
    - jdk
    </#if>
    <#if kits.node??>
    - node
    </#if>
</#if>
<#if machine.synchronization?lower_case == "guard">
    - ruby
    - guard
</#if>