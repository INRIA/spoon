---
title: Spoon Patterns
---

Spoon patterns enables you to find code elements. A Spoon pattern is based on a one or several AST nodes, which represent the code to match, where some parts of the AST are pattern parameters. When a pattern is matched, one can access to the code matched in each pattern parameter.

The main classes of Spoon patterns are those in package `spoon.pattern`:

* classes: PatternBuilder, Pattern, Match, PatternBuilderHelper, PatternParameterConfigurator, InlinedStatementConfigurator 
* eums: ConflictResolutionMode, Quantifier

See also [examples in project `spoon-examples`](https://github.com/SpoonLabs/spoon-examples/blob/master/src/main/java/fr/inria/gforge/spoon/analysis/PatternTest.java)

## Example usage

```java
Factory spoonFactory = ...
Pattern pattern = PatternBuilder.create(mainClass.getMethodsByName("m1").get(0).getBody().clone()).configurePatternParameters().build();

//search for all occurences of the method in the root package
pattern.forEachMatch(spoonFactory.getRootPackage(), (Match match) -> {
	Map<String, Object> parameters = match.getParametersAsMap();
	CtMethod<?> matchingMethod = match.getMatchingElement(CtMethod.class);
	String aNameOfMatchedMethod = parameters.get("methodName");
	...
});
```

## PatternBuilder

To create a Spoon pattern, one must use `PatternBuilder`, which takes AST nodes as input, and _pattern parameters_ can be defined.

```java
// creates pattern from the body of method "matcher1"
elem = mainClass.getMethodsByName("matcher1").get(0).getBody().clone()
Pattern t = PatternBuilder.create(elem)
    .build();
```


If you call `configurePatternParameters()`, all variables that are declared outside of the AST node are automatically declared a pattern parameter. 
```
Pattern t = PatternBuilder.create(elem)
    .configurePatternParameters()
    .build();
```

One can also create specific parameters with `.configureParameters(pb -> pb.parameter("name").byXXXX` (see below)

## Pattern 

Once a `PatternBuilder` returns a `Pattern`, the main methods of `Pattern` are `getMatches` and `forEachMatch`.

```
List<Match> matches = pattern.getMatches(ctClass);
```

## Match

A `Match` represent a match of a pattern on a code elements. The main methods are `getMatchingElement` and `getMatchingElements`.

## PatternBuilderHelper

`PatternBuilderHelper` is useful to select AST nodes that would act as pattern. See method to get the body (method `setBodyOfMethod`) or the return expression of a method (method `setReturnExpressionOfMethod`).

## PatternParameterConfigurator

To create pattern paramters, one uses a `PatternParameterConfigurator` as a lambda:


```java
//a pattern model 
void method(String _x_) {
	zeroOneOrMoreStatements();
	System.out.println(_x_);
}

//a pattern definition
Pattern t = PatternBuilder.create(...select pattern model...)
	.configureParameters(pb -> 
		// creating a pattern parameter called "firstParamName"
		pb.parameter("firstParamName")
			//...select which AST nodes are parameters...
			//e.g. using parameter selector
			.bySimpleName("stmt")
			
		//... you can define as many parameters as you need...
		
		// another parameter (all usages of variable "_x_"
		pb.parameter("lastParamName").byVariable("_x_");
	)
	.build();
```

`ParametersBuilder` has many methods to create the perfect pattern parameters, incl:

* `byType(Class|CtTypeReference|String)` - all the references to the type defined by Class,
CtTypeReference or qualified name are considered as pattern parameter
* `byLocalType(CtType<?> searchScope, String localTypeSimpleName)` - all the types defined in `searchScope`
and having simpleName equal to `localTypeSimpleName` are considered as pattern parameter
* `byVariable(CtVariable|String)` - all read/write variable references to CtVariable
or any variable named with the provided simple name are considered as pattern parameter
* `byInvocation(CtMethod<?> method)` - all invocations of `method` are considered as pattern parameter
* `byVariable(CtVariable|String... variableName)` - each `variableName` is a name of a variable
which references instance of a class with fields. Each such field is considered as pattern parameter.
* `byFilter(Filter)` - any pattern model element, where `Filter.accept(element)` returns true is a pattern parameter.
* `byRole(CtRole role, Filter filter)` - the attribute defined by `role` of all 
pattern model elements, where `Filter.accept(element)` returns true is a pattern parameter. It can be used to define a varible on any CtElement attribute. E.g. method modifiers or throwables, ...
* `byString(String name)` - all pattern model string attributes whose value is equal to `name` are considered as pattern parameter.This can be used to define full name of the methods and fields, etc.
* `bySubstring(String stringMarker)` - all pattern model string attributes whose value contains
whole string or a substring equal to `stringMarker`are pattern parameter. Note: only the `stringMarker` substring of the string value is substituted, other parts of string/element name are kept unchanged.
* `byNamedElement(String name)` - any CtNamedElement identified by it's simple name is a pattern parameter.
* `byReferenceName(String name)` - any CtReference identified by it's simple name is a pattern parameter.


Any parameter of a pattern can be configured like this:

* `setMinOccurence(int)` - defines minimal number of occurences of the value of this parameter during **matching**,
which is needed by matcher to accept that value. 
  * `setMinOccurence(0)` - defines optional parameter
  * `setMinOccurence(1)` - defines mandatory parameter
  * `setMinOccurence(n)` - defines parameter, whose value must be repeated at least n-times
* `setMaxOccurence(int)` - defines maximal number of occurences of the value of this parameter during **matching**,
which is accepted by matcher to accept that value.
* `setMatchingStrategy(Quantifier)` - defines how to matching engine arehave when two pattern nodes may accept the same value.
  * `Quantifier#GREEDY` - Greedy quantifiers are considered "greedy" because they force the matcher to read in, or eat, the entire input prior to attempting the next match.
If the next match attempt (the entire input) fails, the matcher backs off the input by one and tries again,
repeating the process until a match is found or there are no more elements left to back off from.
  * `Quantifier#RELUCTANT` - The reluctant quantifier takes the opposite approach: It start at the beginning of the input,
then reluctantly eat one character at a time looking for a match.
The last thing it tries is the entire input.
  * `Quantifier#POSSESSIVE` - The possessive quantifier always eats the entire input string,
trying once (and only once) for a match. Unlike the greedy quantifiers, possessive quantifiers never back off,
even if doing so would allow the overall match to succeed.
* `setValueType(Class type)` - defines a required type of the value. If defined the pattern matched, will match only values which are assigneable from the provided `type`
* `matchCondition(Class<T> type, Predicate<T> matchCondition)` - defines a `Predicate`, whose method `boolean test(T)`,
are called by pattern matcher. Template matcher accepts that value only if `test` returns true for the value.
The `setValueType(type)` is called internally too, so match condition assures both a type of value and condition on value.
* `setContainerKind(ContainerKind)` - defines what container are used to store the value.
  * `ContainerKind#SINGLE` - only single value is accepted as a parameter value.
  It can be e.g. single String or single CtStatement, etc.
  * `ContainerKind#LIST` - The values are always stored as `List`.
  * `ContainerKind#SET` - The values are always stored as `Set`.
  * `ContainerKind#MAP` - The values are always stored as `Map`.


## InlinedStatementConfigurator

It is possible to match inlined code, eg:

```java
System.out.println(1);
System.out.println(2);
System.out.println(3);
```

can be matched by 

```java
for (int i=0; i<n; i++) {
  System.out.println(n);
}
```

One mark code to be matched inlined using method `configureInlineStatements`, which receives a  InlinedStatementConfigurator as follows:

```java
Pattern t = PatternBuilder.create(...select pattern model...)
	//...configure parameters...
	configureInlineStatements(ls -> 
		//...select to be inlined statements...
		//e.g. by variable name:
		ls.byVariableName("intValues")
	).build();
```

The inlining methods are:

* `inlineIfOrForeachReferringTo(String varName)` - all CtForEach and CtIf statements
whose expression references variable named `varName` are understood as
inline statements
* `markAsInlined(CtForEach|CtIf)` - provided CtForEach or CtIf statement
is understood as inline statement

## Generator

All patterns can be used for code generation. The idea is that one calls `#generator()` on a pattern object to get a `Generator`. This class contains methods that takes as input a map of string,objects where each string key points to a pattern parameter name and each map value contains the element to be put in place of the pattern parameter.

## Notes

The unique feature of Spoon pattern matching is that we are matching on AST trees and not source code text. It means that:

* source code formating is ignored. For example:

```java
void m() {}
//matches with
void	m(){
}
```
* comments are ignored. For example:

```java
void m() {}
//matches with
/**
 javadoc is ignored
*/
/* was public before */ void m(/*this is ignored too*/) {
	//and line comments are ignored too
}
```

* implicit and explicit elements are considered the same. For example:

```java
if (something) 
	list = (List<String>) new ArrayList<>(FIELD_COUNT);
//matches with
if (something) {
	OuterType.this.list = (java.util.List<java.lang.String>) new java.util.ArrayList<java.lang.String>(Constants.FIELD_COUNT);
}
```

* casts are skipped. For example:

```java
f(x);
//matches with
(Object) f(x);
```
