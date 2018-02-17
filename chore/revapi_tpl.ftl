<#--
    Outputs the kind of the element in the same way Revapi does.
    This works with the report element and examines its declaring element (impl of javax.lang.model.element.Element)
-->
<#macro kind element><#compress>
    <#switch element.declaringElement.kind?api.name()>
        <#case "ANNOTATION_TYPE">@interface<#break>
        <#case "ENUM_CONSTANT">field<#break>
        <#case "CONSTRUCTOR">method<#break>
        <#default>${element.declaringElement.kind?api.name()?lower_case}
    </#switch>
</#compress></#macro>
<#--
    This works directly on the javax.lang.model.element.Element and assumes it represents a type.
    For top-level types, just the simple class name is output. Inner classes are supported, too, using the simple
    names of their enclosing classes.
-->
<#macro typeName declaringElement><#compress>
    <#if declaringElement.enclosingElement.kind?api.name() == "PACKAGE">
    ${declaringElement?api.getSimpleName()}
    <#else>
        <@typeName declaringElement=declaringElement.enclosingElement/>.${declaringElement?api.asType()?api.asElement()?api.getSimpleName()}
    </#if>
</#compress></#macro>
<#--
    A short representation of an element (works directly on javax.lang.model.element.Element instances).
    Types are represented by their simple names, methods like type#method(param, types), fields and enum constants
    like type.name and method parameters like type#method(p1, ===p2===) where p2 is the parameter we want to reference.

    This is mostly equivalent to the way Revapi formats the elements with the exception that the types are not fully
    qualified and type parameters are omitted.
-->
<#macro short declaringElement><#compress>
    <#switch declaringElement.kind?api.name()>
        <#case "CLASS"><@typeName declaringElement=declaringElement/><#break>
        <#case "INTERFACE"><@typeName declaringElement=declaringElement/><#break>
        <#case "ANNOTATION_TYPE"><@typeName declaringElement=declaringElement/><#break>
        <#case "ENUM"><@typeName declaringElement=declaringElement/><#break>
        <#case "METHOD"><@short declaringElement=declaringElement.enclosingElement/>#${declaringElement.simpleName}(<#list declaringElement.parameters as param><@typeName declaringElement=param?api.asType()?api.asElement()/><#sep>, </#list>)<#break>
        <#case "CONSTRUCTOR"><@short declaringElement=declaringElement.enclosingElement/>#${declaringElement.simpleName}(<#list declaringElement.parameters as param><@typeName declaringElement=param?api.asType()?api.asElement()/><#sep>, </#list>)<#break>
        <#case "ENUM_CONSTANT"><@short declaringElement=declaringElement.enclosingElement/>.${declaringElement.simpleName}<#break>
        <#case "FIELD"><@short declaringElement=declaringElement.enclosingElement/>.${declaringElement.simpleName}<#break>
        <#case "PARAMETER"><@short declaringElement=declaringElement.enclosingElement.enclosingElement/>#${declaringElement.enclosingElement.simpleName}(<#list declaringElement.enclosingElement.parameters as param><#if param==declaringElement>===<@typeName declaringElement=param?api.asType()?api.asElement()/>===<#else><@typeName declaringElement=param?api.asType()?api.asElement()/></#if><#sep>, </#list>)<#break>
        <#default>${declaringElement.simpleName}
    </#switch>
</#compress></#macro>
<#--
    Finally, the class name shortening pretty printer. The element is a report element.
-->
<#macro pretty element><#compress>
    <@kind element=element/> <@short declaringElement=element.declaringElement/>
</#compress></#macro>
<#--
    The following line appears as commit status, so the core content should appear at the very beginning
-->
API changes: ${reports?size} (Detected by [Revapi](https://github.com/revapi/revapi/))

Old API: <#list analysis.oldApi.archives as archive>${archive.name}<#sep>, </#list> / New API: <#list analysis.newApi.archives as archive>${archive.name}<#sep>, </#list>

<#list reports as report>
<#list report.differences as diff>
|     | ${diff.description!"none"} |
| :---: | :---: |
| Old | <#if report.oldElement??><@pretty element=report.oldElement/><#else>none</#if> |
| New | <#if report.newElement??><@pretty element=report.newElement/><#else>none</#if> |
| Breaking | <#list diff.classification?keys as compat><#if compat?lower_case=="binary">${compat?lower_case}: ${diff.classification?api.get(compat)?lower_case}<#sep>, </#if></#list> |
</#list>
<#sep>

</#sep>
</#list>
