---
title: Template definition
tags: [template]
keywords: template, definition, code, java
last_updated: October 7, 2015
---

Spoon provides developers a way of writing code transformations: 
code templates. Those templates are statically type-checked, in 
order to ensure statically that the generated code will be correct.
Our key idea behind Spoon templates is that they are regular Java code.
Hence, the type-checking is that of the Java compiler itself.

![Overview of Spoon's Templating System]({{ "/images/template-overview.svg" | prepend: site.baseurl }})

A Spoon template is a Java class that is type-checked by the Java compiler, 
then taken as input by the Spoon templating engine to perform a transformation.
This is summarized in Figure above. A Spoon template can be seen as a 
higher-order program, which takes program elements as arguments, and returns a 
transformed program. Like any function, a template can be used in different 
contexts and give different results, depending on its parameters.

```java
public class CheckBoundTemplate extends StatementTemplate {
	TemplateParameter<Collection<?>> _col_;
	@Override
	public void statement() {
		if (_col_.S().size() > 10)
			throw new OutOfBoundException();
	}
}
```

Class `CheckBoundTemplate` defines a Spoon template. This template specifies a 
statement (in method `statement`) that is a precondition to check that a list 
is smaller than a certain size. This piece of code will be injected at the 
beginning of all methods dealing with size-bounded lists. This template has 
one single template parameter called `_col_`, typed by `TemplateParameter` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/template/TemplateParameter.html)). 
In this case, the template parameter is meant to be an expression (`CtExpression`) 
that returns a Collection (see constructor, line 3). All meta-model classes, 
incl. `CtExpression` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtExpression.html)), 
implement interface `TemplateParameter`. A template parameter has a special method 
(named `S`, for Substitution) that is used as a marker to indicate the places where 
a template parameter substitution should occur. For a `CtExpression`, method `S()` 
returns the return type of the expression.

A method `S()` is never executed, its only goal is to get the template statically 
checked. Instead of being executed, the template source code is taken as input by the 
templating engine which is described above. Consequently, the template source is 
well-typed, compiles, but the binary code of the template is thrown away.

There are three kinds of templates: block templates, statement templates and 
expression templates. Their names denote the code grain they respectively address.
