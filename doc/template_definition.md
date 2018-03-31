---
title: Transformation with Templates
tags: [template]
keywords: template, definition, code, java
---

Spoon provides developers a way of writing code transformations called
**code templates**. Those templates are statically type-checked, in
order to ensure statically that the generated code will be correct.

A Spoon template is a regular Java class that taken as input by the Spoon templating engine to perform a transformation.
This is summarized in Figure below. A Spoon template can be seen as a
higher-order program, which takes program elements as arguments, and returns a
transformed program. Like any function, a template can be used in different
contexts and give different results, depending on its parameters.

![Overview of Spoon's Templating System]({{ "/images/template-overview.svg" | prepend: site.baseurl }})

Definition of templates
-----------------------

Class `CheckBoundTemplate` below defines a Spoon template. 

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

This template specifies a
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

Template Instantiation
-------------

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

Kinds of templating
-------------------

There are different kinds of templating.

#### Subclassing `StatementTemplate`
Using method `apply()` enables to get a new statement (see example `CheckBoundTemplate` above)

#### Subclassing `BlockTemplate`
Using method `apply()` enables to get a new block.

#### Subclassing `ExpressionTemplate`
Using method `apply()` enables to get a new expression.  The core template method must be called `expression` and only contain a return with the expression to be templated.

#### Subclassing `ExtensionTemplate`
Using method `apply()` enables to get a new class where all possible templating in all methods. In addition, the following class level transformations are made:

1) methods and field of the templates are injected in the target class

```java
public class ATemplate1 extends ExtensionTemplate {
  int i;
  void foo() {};
}

// inject `i` and `foo` in aCtClass
Substitution.insertAll(aCtClass, new ATemplate1());
```

2) parametrized superinterfaces are injected in the target class

```java
class ATemplate2 extends ExtensionTemplate implements Serializable, A, B {
	// interface templates supports TypeReference
	@Parameter
	Class A;
}

Template t = new ATemplate2();
t.A = Comparable.class
Substitution.insertAll(aCtClass, t);
// aCtClass now implements Serializable and Serializable

```

3) method parameters are replaced

```java
class ATemplate3 extends ExtensionTemplate {
	public void methodWithTemplatedParameters(Object params) {
		// code
	}

	@Parameter
	public List<CtParameter> params;
}

Template t = new ATemplate3();
t.params = ...
Substitution.insertAll(aCtClass, t);
// aCtClass  contains methodmethodWithTemplatedParameters with specific parameters

```

Template parameters
------------------

#### AST elements
All meta-model elements can be used as template parameter. 
There are two ways of defining such a template parameter.

1) Using a subtype of TemplateParameter

The following template uses a block as template parameter.
This template type-checks, and can be used as input by the substitution
engine to wrap a method body into a try/catch block. The substitution engine
contains various methods that implement different substitution scenarios.


```java
public class TryCatchOutOfBoundTemplate extends BlockTemplate {
        // CTBlock is a subtype of TemplateParameter as most metamodel elements
	CtBlock _body_; // the body to surround

	@Override
	public void block() {
		try {
			_body_.S();
		} catch (OutOfBoundException e) {
			e.printStackTrace();
		}
	}
}
```
One can also type the field directly with `TemplateParameter`:

```java
public class TryCatchOutOfBoundTemplate extends BlockTemplate {
	TemplateParameter<Void> _body_; // the body to surround

	@Override
	public void block() {
		try {
			_body_.S();
		} catch (OutOfBoundException e) {
			e.printStackTrace();
		}
	}
}
```


2) Using annotation `@Parameter`

Fields annotated with `@Parameter` are template parameters. 

```java
@Parameter
CtInvocation invocation;
```
and then all `invocation.S()` will be replaced by the actual invocation.


#### Literal template Parameters

For literals, Spoon provides developers with  *literal template parameters*. When the parameter is known to
be a literal (primitive types, `Class` or a one-dimensional array of
these types), a template parameter, annotated with `@Parameter` enables one to have concise template code.

```java
// with literal template parameter
@Parameter
int val;
...
val = 5;
...
if (list.size()>val) {...}
```

String parameters are not working like other primitive type parameters, since we're using String parameters only to rename elements of the code like fields and methods.

```java
// with String template parameter, which is used to substitute method name. 
@Parameter
String methodName;
...
methodName = "generatedMethod";
...
void methodName() {
	//a body of generated method
}
```

To use a parameter with a type String like other primitive types, use CtLiteral<String>.

```java
// with CtLiteral<String> template parameter, which is used to substitute String literal 
@Parameter
CtLiteral<String> val;
...
val = factory.Code().createLiteral("Some string");
...
String someMethod() {
	return val.S();	//is substituted as return "Some string";
}
```

or String literal can be optionally generated like this

```java
// with CtLiteral<String> template parameter, which is used to substitute String literal 
@Parameter
String val;
...
val = "Some string";
...
String someMethod() {
	return "val";	//is substituted as return "Some string";
}
```

#### Inlining foreach expressions

Foreach expressions can be inlined. They have to be declared as follows:

```java
@Parameter
CtExpression[] intValues;
...
template.intValues = new CtExpression[2];
template.intValues[0] = factory.Code().createLiteral(0);
template.intValues[1] = factory.Code().createLiteral(1);
```

and then,

```java
for(Object x : intValues) {
         System.out.println(x);
}
```
is transformed into:

```java
{
    java.lang.System.out.println(0);
    java.lang.System.out.println(1);
}
```
