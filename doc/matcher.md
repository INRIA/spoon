---
title: Matching elements
tags: [quering]
keywords: quering, query, filter, ast, elements
---

Spoon provides a way to declaratively specify a code snippet to match, this is called a `TemplateMatcher`.
For instance, the following snippet matches any if statement parametrized with a collection expression:

```
if (_col_.S().size() > 10)
  throw new IndexOutOfBoundsException();
```

It would match the following code elements:

```
// c is a local variable, a method parameter or a field;
if (c.size() > 10)
  throw new IndexOutOfBoundsException();
---
//foo() returns a collection
if (foo().size() > 10)
  throw new IndexOutOfBoundsException();

```

To define a template matcher one must:

1. specify the "holes" of the template matcher 
1. write the matcher in a dedicated method
1. instantiate TemplateMatcher and call method `find` or use it as Filter of a query.

Taking again the same example.

```
public class CheckBoundMatcher {
  // Step 1:
  public TemplateParameter<Collection<?>> _col_;
  
  // Step 2
  public void matcher1() {
    if (_col_.S().size() > 10)
      throw new IndexOutOfBoundsException();
  }
}

// Step 3, for instance in a main
// where to find the matching specification
CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NameFilter("matcher1")).get(0)).getBody().getStatement(0);
TemplateMatcher matcher = new TemplateMatcher(templateRoot);
for (CtElement elems : matcher.find(aPackage)) { ... };
//or TemplateMatcher as a Filter of query
aPackage.filterChildren(matcher).forEach((CtElement elem)->{ ... });
```

For named elements, a wildcard can be specified: if the named element (eg a method) to be matched is called `f` and the template matcher class contains a template parameter called `f` (of type Object), all methods starting by `f` will be matched.

Note, the matching process ignores some information in the AST nodes: comments; position; implicitness and casts. See `roleToSkippedClass` in class [ElementNode](https://github.com/INRIA/spoon/blob/master/src/main/java/spoon/pattern/internal/node/ElementNode.java)

