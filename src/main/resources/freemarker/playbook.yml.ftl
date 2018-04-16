---
- hosts: all
  roles:
    - applications
    - commons
<#if containers?? && containers?size gt 0>
    - docker
</#if>
<#if kits.guard??>
    - jdk
</#if>
<#if kits.guard??>
    - node
</#if>
<#if kits.guard??>
    - guard
</#if>