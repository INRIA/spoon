---
title: Matching elements
tags: [quering]
keywords: quering, query, filter, ast, elements
last_updated: March 26, 2016
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
if (foo() > 10)
  throw new IndexOutOfBoundsException();

```

To define a template matcher one must:

1. specify the "holes" of the template matcher 
1. write the matcher in a dedicated method
1. instantiate TemplateMatcher and call method `find`.

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
}

// Step 3, for instance in a main
// where to find the matching specification
CtClass<?> templateKlass = factory.Class().get(CheckBoundMatcher.class);
CtIf templateRoot = (CtIf) ((CtMethod) templateKlass.getElements(new NameFilter("matcher1")).get(0)).getBody().getStatement(0);
TemplateMatcher matcher = new TemplateMatcher(templateRoot);
for (CtElement elems : matcher.find(aPackage)) { ... };
			
```

