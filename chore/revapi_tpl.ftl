# Revapi Analysis results

Old API: **<#list analysis.oldApi.archives as archive>${archive.name}<#sep>, </#list>**

New API: **<#list analysis.newApi.archives as archive>${archive.name}<#sep>, </#list>**

<#list reports as report>
<#list report.differences as diff>
| Name | Element |
| :---: | :---: |
| Old | ${report.oldElement!"none"} |
| New | ${report.newElement!"none"} |
| Code | ${diff.code} |
| Description | ${diff.description!"none"} |
| Breaking | <#list diff.classification?keys as compat>${compat}?lower_case: ${diff.classification?api.get(compat)}?lower_case<#sep>, </#list> |
</#list>
<#sep>

</#sep>
</#list>