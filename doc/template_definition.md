---
title: Transformation with Templates
tags: [template]
keywords: template, definition, code, java
---

Spoon provides developers a way to define so called
**Spoon templates**. **Spoon template** is a part of code (for example  
expression, statement, block, method, constuctor, type member, 
interface, type, ... any Spoon model subtree),
where parts of that code may be **template parameters**.

**Spoon template** can be used in two ways:

A) **to generate new code**. The generated code is a copy of code
of **Spoon template**, where each **template parameter** is substituted
by it's value. We call this operation **Generating**.

```java
Factory spoonFactory = ...
//build a Spoon template
Pattern spoonTemplate = ... build a spoon template. For example for an method ...
//define values for parameters
Map<String, Object> parameters = new HashMap<>();
parameters.put("methodName", "i_am_an_generated_method");
//generate a code using spoon template and parameters
CtMethod<?> generatedMethod = spoonTemplate.substituteSingle(spoonFactory, CtMethod.class, parameters);
```

This is summarized in Figure below. A Spoon template can be seen as a
higher-order program, which takes program elements as arguments, and returns a
transformed program. Like any function, a template can be used in different
contexts and give different results, depending on its parameters.

![Overview of Spoon's Templating System]({{ "/images/template-overview.svg" | prepend: site.baseurl }})

B) **to search for a code**. The found code is same like code of **Spoon template**,
where code on position of **template parameter** may be arbitrary and is copied
as value of **template parameter**. We call this operation **Matching**.

```java
Factory spoonFactory = ...
//build a Spoon template
Pattern spoonTemplate = ... build a spoon template. For example for an method ...
//search for all occurences of the method like spoonTemplate in whole model
spoonTemplate.forEachMatch(spoonFactory.getRootPackage(), (Match match) -> {
	//this Consumer is called once for each method which matches with spoonTemplate
	Map<String, Object> parameters = match.getParametersAsMap();
	CtMethod<?> matchingMethod = match.getMatchingElement(CtMethod.class);
	String aNameOfMatchedMethod = parameters.get("methodName");
	...
});
```

There are several ways how to build a **spoon template**

* Using a regular java class, which implements a `Template` interface

* Using PatternBuilder, which takes any part of code and where you 
define which parts of that code are **template parameters** by calling PatternBuilder methods.

The `Template` interface based definitions are statically type-checked, in order to ensure statically that the generated code will be correct.
Both template definitions are normal compiled java source code,
which is part of your sources, so:
* if the template source is compilable then generated code will be compilable too - when you use correct parameter values of course.
* the refactoring applied to your source code is automatically applied to your templates too. So the maintenance effort of Spoon templates is lower comparing to effort needed to maintain third party templates based on concatenation of strings.

Definition of `Template` interface based templates
--------------------------------------------------

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

Definition of templates using `PatternBuilder`
--------------------------------------------------

The body of method `CheckBoundTemplate#statement` below defines a Spoon template. 

```java
public class CheckBoundTemplate /*it doesn't matter what it extends or implements*/ {
	// it doesn't matter what other type members are in template class
	// it doesn't matter what is the name of the method whose body will be used as template
	public void statement(Collection<?> _col_) {
		if (_col_.size() > 10)
			throw new OutOfBoundException();
	}
}
```

The code, which creates a Spoon template using `PatternBuilder` looks like this:

```java
Pattern t = PatternBuilder.create(factory,
	//defines template class.
	CheckBoundTemplate.class, 
	//defines which part of template class will be used as template model
	model -> model.setBodyOfMethod("statement"))
	//tells builder that all variables defined out of scope of template model (body of the method)
	//are considered as template parameters
	.configureAutomaticParameters()
	//builds an instance of Pattern
	.build();
```


This template specifies a
statements (all statements of body of method `statement`) that is a precondition to check that a list 
is smaller than a certain size. This piece of code will be injected at the 
beginning of all methods dealing with size-bounded lists. This template has 
one single template parameter called `_col_`. 
In this case, the template parameter value is meant to be an expression (`CtExpression`) 
that returns a Collection.

The template source is 
well-typed, compiles, but the binary code of the template is usually thrown away
and only spoon model (Abstract syntax tree) of source code is used to generated new code
or to search for matching code.

Generating of code using Spoon template
-------------

The code at the end of this page shows how to use such spoon template.
One takes a spoon template, defines the template parameters, 
and then one calls the template engine. In last line, the bound check 
is injected at the beginning of a method body. 

Since the template is given the first method parameter which is in the 
scope of the insertion location, the generated code is guaranteed to compile. 
The Java compiler ensures that the template compiles with a given scope, the 
developer is responsible for checking that the scope where she uses 
template-generated code is consistent with the template scope.

```java
Pattern t = PatternBuilder.create(...building a template. See code above...);
// creating a holder of parameters
Map<String, Object> parameters = new HashMap<>();
parameters.put("_col_", createVariableAccess(method.getParameters().get(0))); 

// getting the final AST
CtStatement injectedCode = t.substituteSingle(factory, CtStatement.class, parameters);

// adds the bound check at the beginning of a method
method.getBody().insertBegin(injectedCode);
```

Kinds of `Template` interface based templating
---------------

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

## PatternBuilder parameters
The `PatternBuilder` takes all the Template parameters mentioned in the chapters above
and understands them as template parameters, when `PatternBuilder#configureTemplateParameters()`
is called.

```java
Pattern t = PatternBuilder.create(...select template model...)
	.configureTemplateParameters()
	.build();
```

Next to the ways of parameter definitions mentioned above the `PatternBuilder`
allows to define parameters like this:

```java
//a template model 
void method(String _x_) {
	zeroOneOrMoreStatements();
	System.out.println(_x_);
}

//a pattern definition
Pattern t = PatternBuilder.create(...select template model...)
	.configureParameters(pb -> 
		pb.parameter("firstParamName")
			//...select which AST nodes are parameters...
			//e.g. using parameter selector
			.bySimpleName("zeroOneOrMoreStatements")
			//...modify behavior of parameters...
			//e.g. using parameter modifier
			.setMinOccurence(0);
			
		//... you can define as many parameters as you need...
		
		pb.parameter("lastParamName").byVariable("_x_");
	)
	.build();
```

## PatternBuilder parameter selectors

* `byType(Class|CtTypeReference|String)` - all the references to the type defined by Class,
CtTypeReference or qualified name will be considered as template parameter
* `byLocalType(CtType<?> searchScope, String localTypeSimpleName)` - all the types defined in `searchScope`
and having simpleName equal to `localTypeSimpleName` will be considered as template parameter
* `byVariable(CtVariable|String)` - all read/write variable references to CtVariable
or any variable with provided simple name will be considered as template parameter
* byInvocation(CtMethod<?> method) - each invocation of `method` will be considered as template parameter
* `parametersByVariable(CtVariable|String... variableName)` - each `variableName` is a name of a variable
which references instance of a class with fields. Each such field is considered as template parameter.
* `byTemplateParameterReference(CtVariable)` - the reference to variable of type `TemplateParameter` is handled
as template parameter using all the rules defined in the chapters above.
* `byFilter(Filter)` - any template model element, where `Filter.accept(element)` returns true is a template parameter.
* `attributeOfElementByFilter(CtRole role, Filter filter)` - the attribute defined by `role` of all 
template model elements, where `Filter.accept(element)` returns true is a template parameter.
It can be used to define a varible on any CtElement attribute. E.g. method modifiers or throwables, ...
* `byString(String name)` - all template model string attributes whose value **is equal to** `name` are considered as template parameter.This can be used to define full name of the methods and fields, etc.
* `bySubstring(String stringMarker)` - all template model string attributes whose value **contains**
whole string or a substring equal to `stringMarker`are template parameter.
Note: only the `stringMarker` substring of the string value is substituted.
Other parts of string/element name are kept unchanged.
* `bySimpleName(String name)` - any CtNamedElement or CtReference identified by it's simple name is a template parameter.
* `byNamedElementSimpleName(String name)` - any CtNamedElement identified by it's simple name is a template parameter.
* `byReferenceSimpleName(String name)` - any CtReference identified by it's simple name is a template parameter.

Note: 
* `byString` and `bySubstring` are used to rename code elements.
For example to rename a method "xyz" to "abc"
* `bySimpleName`, `byNamedElementSimpleName`, `byReferenceSimpleName`
are used to replace these elements by completelly different elements.
For example to replace method invocation by an variable reference, etc.


## PatternBuilder parameter modifiers
Any parameter of spoon template can be configured like this:

* `setMinOccurence(int)` - defines minimal number of occurences of the value of this parameter during **matching**,
which is needed by matcher to accept that value. 
  * `setMinOccurence(0)` - defines optional parameter
  * `setMinOccurence(1)` - defines mandatory parameter
  * `setMinOccurence(n)` - defines parameter, whose value must be repeated at least n-times
* `setMaxOccurence(int)` - defines maximal number of occurences of the value of this parameter during **matching**,
which is accepted by matcher to accept that value.
* `setMatchingStrategy(Quantifier)` - defines how to matching engine will behave when two template nodes may accept the same value.
  * `Quantifier#GREEDY` - Greedy quantifiers are considered "greedy" because they force the matcher to read in, or eat,
the entire input prior to attempting the next match.
If the next match attempt (the entire input) fails, the matcher backs off the input by one and tries again,
repeating the process until a match is found or there are no more elements left to back off from.
  * `Quantifier#RELUCTANT` - The reluctant quantifier takes the opposite approach: It start at the beginning of the input,
then reluctantly eat one character at a time looking for a match.
The last thing it tries is the entire input.
  * `Quantifier#POSSESSIVE` - The possessive quantifier always eats the entire input string,
trying once (and only once) for a match. Unlike the greedy quantifiers, possessive quantifiers never back off,
even if doing so would allow the overall match to succeed.
* `setValueType(Class type)` - defines a required type of the value. If defined the template matched, will match only values which are assigneable from the provided `type`
* `matchCondition(Class<T> type, Predicate<T> matchCondition)` - defines a `Predicate`, whose method `boolean test(T)`,
will be called by template matcher. Template matcher accepts that value only if `test` returns true for the value.
The `setValueType(type)` is called internally too, so match condition assures both a type of value and condition on value.
* `setContainerKind(ContainerKind)` - defines what container will be used to store the value.
  * `ContainerKind#SINGLE` - only single value is accepted as a parameter value.
  It can be e.g. single String or single CtStatement, etc.
  * `ContainerKind#LIST` - The values are always stored as `List`.
  * `ContainerKind#SET` - The values are always stored as `Set`.
  * `ContainerKind#MAP` - The values are always stored as `Map`.

#### Inlining foreach expressions

All Foreach expressions, which contains a template paremeter are inlined
in `Template` interface based templates. They have to be declared as follows:

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
## Inlining with PatternBuilder
The template code in spoon templates made by `PatternBuilder` is never inlined automatically.
But you can mark code to be inlined this way:
```java
Pattern t = PatternBuilder.create(...select template model...)
	//...configure parameters...
	configureInlineStatements(ls -> 
		//...select to be inlined statements...
		//e.g. by variable name:
		ls.byVariableName("intValues")
	).build();
```

### PatternBuilder inline statements selectors

* `byVariableName(String varName)` - all CtForEach and CtIf statements
whose expression references variable named `varName` are understood as
inline statements
* `markInline(CtForEach|CtIf)` - provided CtForEach or CtIf statement
is understood as inline statement
