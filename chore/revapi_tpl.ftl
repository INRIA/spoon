# Revapi Analysis results

Old API: **<#list analysis.oldApi.archives as archive>${archive.name}<#sep>, </#list>**

New API: **<#list analysis.newApi.archives as archive>${archive.name}<#sep>, </#list>**

Old | New | Code | Description | Breaking
:---: | :---: | :---: | :---: | :---:
<#list reports as report>
<#list report.differences as diff>
${report.oldElement!"none"} | ${report.newElement!"none"} | ${diff.code} | ${diff.description!"none"} | <#list diff.classification?keys as compat>${compat}: ${diff.classification?api.get(compat)}<#sep>, </#list> |
</#list>
</#list>
</table>