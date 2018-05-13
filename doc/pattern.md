---
title: Spoon Patterns
---

**Spoon patterns** aim at finding code elements using patterns. A **Spoon pattern** is based on a one or several AST nodes,
where some parts of the AST are **pattern parameters**.

Once you have a pattern, one matches again some code:

```java
Factory spoonFactory = ...
//build a Spoon pattern
Pattern pattern = ... build a spoon pattern. For example for an method ...

//search for all occurences of the method in the root package
pattern.forEachMatch(spoonFactory.getRootPackage(), (Match match) -> {
	Map<String, Object> parameters = match.getParametersAsMap();
	CtMethod<?> matchingMethod = match.getMatchingElement(CtMethod.class);
	String aNameOfMatchedMethod = parameters.get("methodName");
	...
});
```


## Main class: `PatternBuilder`

To create a Spoon pattern, one must use `PatternBuilder`, which takes AST nodes as input, and where you 
**pattern parameters** are defined.


The code to creates a Spoon pattern using `PatternBuilder` looks like this:

```java
public class Foo {
	public void statement() {
		if (_col_.size() > 10)
			throw new OutOfBoundException();
	}
}
```


```java
Pattern t = PatternBuilder.create(
    new PatternBuilderHelper(fooClass).setBodyOfMethod("matcher1").getPatternElements())
    .configureParameters()
    .build();
```


This pattern matches all statements of the body of method `statement`, ie. a precondition to check that a list 
is smaller than a certain size. 
This pattern has 
one single pattern parameter called `_col_`, which is considered as a pattern parameter because it is declared outside of the AST node. 

## ParametersBuilder parameters

To create pattern, one use a `ParametersBuilder` in a lambda:


```java
//a pattern model 
void method(String _x_) {
	zeroOneOrMoreStatements();
	System.out.println(_x_);
}

//a pattern definition
Pattern t = PatternBuilder.create(...select pattern model...)
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

`ParametersBuilder` has the following methods:

* `byType(Class|CtTypeReference|String)` - all the references to the type defined by Class,
CtTypeReference or qualified name are considered as pattern parameter
* `byLocalType(CtType<?> searchScope, String localTypeSimpleName)` - all the types defined in `searchScope`
and having simpleName equal to `localTypeSimpleName` are considered as pattern parameter
* `byVariable(CtVariable|String)` - all read/write variable references to CtVariable
or any variable with provided simple name are considered as pattern parameter
* `byInvocation(CtMethod<?> method)` - each invocation of `method` are considered as pattern parameter
* `parametersByVariable(CtVariable|String... variableName)` - each `variableName` is a name of a variable
which references instance of a class with fields. Each such field is considered as pattern parameter.
* `byTemplateParameterReference(CtVariable)` - the reference to variable of type `TemplateParameter` is handled
as pattern parameter using all the rules defined in the chapters above.
* `byFilter(Filter)` - any pattern model element, where `Filter.accept(element)` returns true is a pattern parameter.
* `attributeOfElementByFilter(CtRole role, Filter filter)` - the attribute defined by `role` of all 
pattern model elements, where `Filter.accept(element)` returns true is a pattern parameter. It can be used to define a varible on any CtElement attribute. E.g. method modifiers or throwables, ...
* `byString(String name)` - all pattern model string attributes whose value is equal to `name` are considered as pattern parameter.This can be used to define full name of the methods and fields, etc.
* `bySubstring(String stringMarker)` - all pattern model string attributes whose value contains
whole string or a substring equal to `stringMarker`are pattern parameter.
Note: only the `stringMarker` substring of the string value is substituted.
Other parts of string/element name are kept unchanged.
* `bySimpleName(String name)` - any CtNamedElement or CtReference identified by it's simple name is a pattern parameter.
* `byNamedElementSimpleName(String name)` - any CtNamedElement identified by it's simple name is a pattern parameter.
* `byReferenceSimpleName(String name)` - any CtReference identified by it's simple name is a pattern parameter.


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


## Inlining with PatternBuilder

It is possible to inlined code, eg:

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


But you can mark code to be matched inlined as follows:
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

* `byVariableName(String varName)` - all CtForEach and CtIf statements
whose expression references variable named `varName` are understood as
inline statements
* `markInline(CtForEach|CtIf)` - provided CtForEach or CtIf statement
is understood as inline statement

