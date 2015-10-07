---
title: Literal Template Parameter
tags: [template]
keywords: template, substitution, code, java
last_updated: October 7, 2015
---

We have already seen one kind of template parameter (`TemplateParameter<T>` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/template/TemplateParameter.html))). 
Sometimes, templates are parameterized literal values. This can be done with 
a template parameter set to a `CtLiteral` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtLiteral.html)), 
for instance,

```
// with TemplateParameter
TemplateParameter<Integer> val;
...
val = Factory.createLiteral(5);
...
if (list.size()>val.S()) {...}

// with literal template parameter
@Parameter
int val;
...
val = 5;
...
if (list.size()>val) {...}
```

For convenience, Spoon provides developers with another kind of template 
parameters called *literal template parameters*. When the parameter is known to 
be a literal (primitive types, `String`, `Class` or a one-dimensional array of 
these types), a template parameter enables one to simplify the template code. 
To indicate to the substitution engine that a given field is a template parameter, 
it has to be annotated with a `@Parameter` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/template/Parameter.html)) 
annotation. The code above illustrates this feature with two equivalent templates. 
By using a literal template parameter, it is not necessary to call the `S()` method 
for substitution: the templating engine looks up all usages of the field annotated with 
`@Parameter`. The listing above shows those differences.