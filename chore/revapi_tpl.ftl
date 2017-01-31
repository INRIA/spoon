# Revapi Analysis results

Old API: **<#list analysis.oldApi.archives as archive>${archive.name}<#sep>, </#list>**

New API: **<#list analysis.newApi.archives as archive>${archive.name}<#sep>, </#list>**

<table>
<tr>
    <th>Old</th>
    <th>New</th>
    <th>Code</th>
    <th>Description</th>
    <th>Breaking</th>
</tr>
<#list reports as report>
<#list report.differences as diff>
<tr>
    <td style="text-align:center">${report.oldElement!"none"}</td>
    <td style="text-align:center">${report.newElement!"none"}</td>
    <td style="text-align:center">${diff.code}</td>
    <td style="text-align:center">${diff.description!"none"}</td>
    <td style="text-align:center"><#list diff.classification?keys as compat>${compat}: ${diff.classification?api.get(compat)}<#sep>, </#list></td>
</tr>
</#list>
</#list>
</table>