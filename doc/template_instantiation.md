---
title: Template instantiation
tags: [template]
keywords: template, instantiation, code, java
last_updated: October 7, 2015
---

In order to be correctly substituted, the template parameters need 
to be bound to actual values. This is done during template instantiation.

The code at the end of this page shows how to use the check-bound 
of template, `CheckBoundTemplate`, presented in the previous section. 
One first instantiates a template, then one sets the template parameters, 
and finally, one calls the template engine. In last line, the bound check 
is injected at the beginning of a method body. 

Since the template is given the first method parameter which is in the 
scope of the insertion location, the generated code is guaranteed to compile. 
The Java compiler ensures that the template compiles with a given scope, the 
developer is responsible for checking that the scope where she uses 
template-generated code is consistent with the template scope.

```java
// creating a template instance
Template t = new CheckBoundTemplate();
t._col_ = createVariableAccess(method.getParameters().get(0)); 

// getting the final AST
CtStatement injectedCode = t.apply();

// adds the bound check at the beginning of a method
method.getBody().insertBegin(injectedCode);
```