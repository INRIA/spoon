---
title: Spoon Patterns
---

**Spoon pattern's** aimas at matching and transforming code elements. A **Spoon pattern** is based on a code element (for example  
expression, statement, block, method, constuctor, type member, 
interface, type, ... any Spoon model subtree),
where parts of that code may be **pattern parameters**.

**Spoon pattern's** can be used in two ways:

A) **to search for a code**. The found code is same like code of **Spoon pattern**,
where code on position of **pattern parameter** may be arbitrary and is copied
as value of **pattern parameter**. We call this operation **Matching**.

```java
Factory spoonFactory = ...
//build a Spoon pattern
Pattern spoonTemplate = ... build a spoon pattern. For example for an method ...
//search for all occurences of the method like spoonTemplate in whole model
spoonTemplate.forEachMatch(spoonFactory.getRootPackage(), (Match match) -> {
	//this Consumer is called once for each method which matches with spoonTemplate
	Map<String, Object> parameters = match.getParametersAsMap();
	CtMethod<?> matchingMethod = match.getMatchingElement(CtMethod.class);
	String aNameOfMatchedMethod = parameters.get("methodName");
	...
});
```

B) **to generate new code**. The generated code is a copy of code
of **Spoon pattern**, where each **pattern parameter** is substituted
by it's value. We call this operation **Generating**.

```java
Factory spoonFactory = ...
//build a Spoon pattern
Pattern spoonTemplate = ... build a spoon pattern. For example for an method ...
//define values for parameters
Map<String, Object> parameters = new HashMap<>();
parameters.put("methodName", "i_am_an_generated_method");
//generate a code using spoon pattern and parameters
CtMethod<?> generatedMethod = spoonTemplate.substituteSingle(spoonFactory, CtMethod.class, parameters);
```

Main class: `PatternBuilder`
--------------------------------------------------

To create a Spoon pattern, one must use `PatternBuilder`, which takes AST nodes as input, and where you 
**pattern parameters** are defined by calling PatternBuilder fluent API methods.


The method `statement` below defines a Spoon pattern. 

```java
public class Foo {
	public void statement() {
		if (_col_.size() > 10)
			throw new OutOfBoundException();
	}
}
```

The code, which creates a Spoon pattern using `PatternBuilder` looks like this:

```java
Pattern t = PatternBuilder.create(factory,
	//defines pattern class.
	CheckBoundTemplate.class, 
	//defines which part of pattern class will be used as pattern model
	model -> model.setBodyOfMethod("statement"))
	//tells builder that all variables defined out of scope of pattern model (body of the method)
	//are considered as pattern parameters
	// here _col_
	.configureTemplateParameters()
	//builds an instance of Pattern
	.build();
```


This pattern specifies a
statements (all statements of body of method `statement`) that is a precondition to check that a list 
is smaller than a certain size. This piece of code will be injected at the 
beginning of all methods dealing with size-bounded lists. This pattern has 
one single pattern parameter called `_col_`. 
In this case, the pattern parameter value is meant to be an expression (`CtExpression`) 
that returns a Collection.

The pattern source is 
well-typed, compiles, but the binary code of the pattern is usually thrown away
and only spoon model (Abstract syntax tree) of source code is used to generated new code
or to search for matching code.

Generating of code using Spoon pattern
-------------

The code at the end of this page shows how to use such spoon pattern.
One takes a spoon pattern, defines the pattern parameters, 
and then one calls the pattern engine. In last line, the bound check 
is injected at the beginning of a method body. 

Since the pattern is given the first method parameter which is in the 
scope of the insertion location, the generated code is guaranteed to compile. 
The Java compiler ensures that the pattern compiles with a given scope, the 
developer is responsible for checking that the scope where she uses 
pattern-generated code is consistent with the pattern scope.

```java
Pattern t = PatternBuilder.create(...building a pattern. See code above...);
// creating a holder of parameters
Map<String, Object> parameters = new HashMap<>();
parameters.put("_col_", createVariableAccess(method.getParameters().get(0))); 

// getting the final AST
CtStatement injectedCode = t.substituteSingle(factory, CtStatement.class, parameters);

// adds the bound check at the beginning of a method
method.getBody().insertBegin(injectedCode);
```

## PatternBuilder parameters
The `PatternBuilder` takes all the Template parameters mentioned in the chapters above
and understands them as pattern parameters, when `PatternBuilder#configureTemplateParameters()`
is called.

```java
Pattern t = PatternBuilder.create(...select pattern model...)
	.configureTemplateParameters()
	.build();
```

Next to the ways of parameter definitions mentioned above the `PatternBuilder`
allows to define parameters like this:

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

## PatternBuilder parameter selectors

* `byType(Class|CtTypeReference|String)` - all the references to the type defined by Class,
CtTypeReference or qualified name will be considered as pattern parameter
* `byLocalType(CtType<?> searchScope, String localTypeSimpleName)` - all the types defined in `searchScope`
and having simpleName equal to `localTypeSimpleName` will be considered as pattern parameter
* `byVariable(CtVariable|String)` - all read/write variable references to CtVariable
or any variable with provided simple name will be considered as pattern parameter
* byInvocation(CtMethod<?> method) - each invocation of `method` will be considered as pattern parameter
* `parametersByVariable(CtVariable|String... variableName)` - each `variableName` is a name of a variable
which references instance of a class with fields. Each such field is considered as pattern parameter.
* `byTemplateParameterReference(CtVariable)` - the reference to variable of type `TemplateParameter` is handled
as pattern parameter using all the rules defined in the chapters above.
* `byFilter(Filter)` - any pattern model element, where `Filter.accept(element)` returns true is a pattern parameter.
* `attributeOfElementByFilter(CtRole role, Filter filter)` - the attribute defined by `role` of all 
pattern model elements, where `Filter.accept(element)` returns true is a pattern parameter.
It can be used to define a varible on any CtElement attribute. E.g. method modifiers or throwables, ...
* `byString(String name)` - all pattern model string attributes whose value **is equal to** `name` are considered as pattern parameter.This can be used to define full name of the methods and fields, etc.
* `bySubstring(String stringMarker)` - all pattern model string attributes whose value **contains**
whole string or a substring equal to `stringMarker`are pattern parameter.
Note: only the `stringMarker` substring of the string value is substituted.
Other parts of string/element name are kept unchanged.
* `bySimpleName(String name)` - any CtNamedElement or CtReference identified by it's simple name is a pattern parameter.
* `byNamedElementSimpleName(String name)` - any CtNamedElement identified by it's simple name is a pattern parameter.
* `byReferenceSimpleName(String name)` - any CtReference identified by it's simple name is a pattern parameter.

Note: 
* `byString` and `bySubstring` are used to rename code elements.
For example to rename a method "xyz" to "abc"
* `bySimpleName`, `byNamedElementSimpleName`, `byReferenceSimpleName`
are used to replace these elements by completelly different elements.
For example to replace method invocation by an variable reference, etc.


## PatternBuilder parameter modifiers
Any parameter of spoon pattern can be configured like this:

* `setMinOccurence(int)` - defines minimal number of occurences of the value of this parameter during **matching**,
which is needed by matcher to accept that value. 
  * `setMinOccurence(0)` - defines optional parameter
  * `setMinOccurence(1)` - defines mandatory parameter
  * `setMinOccurence(n)` - defines parameter, whose value must be repeated at least n-times
* `setMaxOccurence(int)` - defines maximal number of occurences of the value of this parameter during **matching**,
which is accepted by matcher to accept that value.
* `setMatchingStrategy(Quantifier)` - defines how to matching engine will behave when two pattern nodes may accept the same value.
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
* `setValueType(Class type)` - defines a required type of the value. If defined the pattern matched, will match only values which are assigneable from the provided `type`
* `matchCondition(Class<T> type, Predicate<T> matchCondition)` - defines a `Predicate`, whose method `boolean test(T)`,
will be called by pattern matcher. Template matcher accepts that value only if `test` returns true for the value.
The `setValueType(type)` is called internally too, so match condition assures both a type of value and condition on value.
* `setContainerKind(ContainerKind)` - defines what container will be used to store the value.
  * `ContainerKind#SINGLE` - only single value is accepted as a parameter value.
  It can be e.g. single String or single CtStatement, etc.
  * `ContainerKind#LIST` - The values are always stored as `List`.
  * `ContainerKind#SET` - The values are always stored as `Set`.
  * `ContainerKind#MAP` - The values are always stored as `Map`.


## Inlining with PatternBuilder
The pattern code in spoon patterns made by `PatternBuilder` is never inlined automatically.
But you can mark code to be inlined this way:
```java
Pattern t = PatternBuilder.create(...select pattern model...)
	//...configure parameters...
	configureInlineStatements(ls -> 
		//...select to be inlined statements...
		//e.g. by variable name:
		ls.byVariableName("intValues")
	).build();
```

## PatternBuilder inline statements selectors

* `byVariableName(String varName)` - all CtForEach and CtIf statements
whose expression references variable named `varName` are understood as
inline statements
* `markInline(CtForEach|CtIf)` - provided CtForEach or CtIf statement
is understood as inline statement

## Notes

Template definition
1) compilable easy understandable Template
2) The good names of template parameters - well assigned to AST nodes
3) even the attributes of AST nodes might be a parameters (e.g. modifier of class)
 
Notes:
- it doesn't matter what is the current AST node at place of parameter. It will be replaced by parameter value converted to instance of expected type
- we have to define which Types, methodNames are variables and which already have required value 
- we have to define which statements, expressions are optional/mandatory
	e.g. by
	if (optional1) {
		...some optional statements...
	}

Generation of code from Template
1) filling template parameters by values
2) cloning template AST
3) substituting cloned AST parameter nodes by values

Types of template parameters
A) AST node of type CtStatement, CtExpression (e.g. CtVariableAccess, ...)
B) replacing of CtTypeReference by another CtTypeReference
C) replacing of whole or part of simpleName or Reference.name by String value
D) replacing of any attribute of AST node by value of appropriate type

Searching for code, which matches template
1) definition of filters on searched nodes
2) matching with AST
3) creating of Map/Structure of parameter to matching value from AST


{@link Pattern} knows the AST of the pattern model.
It knows list of parts of pattern model, which are target for substitution.

The substitution target can be:
A) node replace parameters - it means whole AST node (subtree) is replaced by value of parameter
		The type of such value is defined by parent attribute which holds this node:
		Examples: CtTypeMember, CtStatement, CtExpression, CtParameter, ...
		A1) Single node replace parameter - there must be exactly one node (with arbitrary subtree) as parameter value
			Examples:
				CtCatch.parameter, CtReturn.expression, CtBinaryOperator.leftOperand,
				CtForEach.variable,
				CtLoop.body,
				CtField.type
				 ...
		A2) Multiple nodes replace parameter - there must be 0, 1 or more nodes (with arbitrary subtrees) as parameter value
			Examples:
				CtType.interfaces, CtType.typeMembers
		note: There can be 0 or 1 parameter assigned to model node

Definition of such CtElement based parameters:
------------------------------
- by `TemplateParameter.S()` - it works only for some node types. Does not work for CtCase, CtCatch, CtComment, CtAnnotation, CtEnumValue, ...
- by pointing to such node(s) - it works for all nodes. How? During  building of Pattern, the client's code has to somehow select the parameter nodes
			and add them into list of to be substituted nodes. Client may use
			- Filter ... here we can filter for `TemplateParameter.S()`
			- CtPath ... after it is fully implemented
			- Filtering by their name - legacy templates are using that approach together with Parameter annotation
			- manual navigation and collecting of substituted nodes

B) node attribute replace - it means value of node attribute is replaced by value of parameter
		B1) Single value attribute - there must be exactly one value as parameter value
			Types are String, boolean, BinaryOperatorKind, UnaryOperatorKind, CommentType, primitive type of Literal.value
		B2) Unordered multiple value attribute - there must be exactly one value as parameter value
			There is only: CtModifiable.modifiers with type Enum ModifierKind

		note: There can be no parameter of type (A) assigned to node whose attributes are going to be replaced.
			There can be more attributes replaced for one node
			But there can be 0 or 1 parameter assigned to attribute of model node

Definition of such Object based parameters:
------------------------------------------------------
by pointing to such node(s)
	+ with specification of CtRole of that attribute

C) Substring attribute replace - it means substring of string value is replaced
		Examples: CtNamedElement.simpleName, CtStatement.label, CtComment.comment

		note: There can be no parameter of type (A) assigned to node whose String attributes are going to be replaced.
			There can be 0, 1 or more parameter assigned to String of model node. Each must have different identifier.

Definition of such parameters:
------------------------------
by pointing to such node(s)
	+ with specification of CtRole of that attribute
	+ with specification of to be replaced substring
It can be done by searching in all String attributes of each node and searching for a variable marker. E.g. "$var_name$"

Optionally there might be defined a variable value formatter, which assures that variable value is converted to expected string representation

Why {@link Pattern} needs such high flexibility?
Usecase: The Pattern instance might be created by comparing of two similar models (not templates, but part of normal code).
All the differences anywhere would be considered as parameters of generated Pattern instance.
Request: Such pattern instance must be printable and compilable, so client can use it for further matching and replacing by different pattern.


Why ParameterInfo type?
----------------------
Can early check whether parameter values can be accepted by Pattern
Needs a validation by SubstitutionRequests of ParameterInfo
Can act as a filter of TemplateMatcher parameter value


Matching algorithms
-------------------
  
There are following kinds of Matching algorithms:

MA-1) matching of one target value with one Matcher
Target value can be 
A) single CtElement, single String, Enum
B) List of T, Set of T , Map of String to T, where T is a type defined above

Input: 
- matcher - to be matched Matcher. Supports `Constant Matcher`, `Variable Matcher`
- parameters - input Parameters
- target - to be matched target object

Output:
- status - 'matched' only if whole target value matched with this Matcher. 'not matched' if something did not matched or if there remained some unmatched items.
- (if status==matched) parameters -  matched parameter values

MA-2) matching of container of targets with one Matcher
Target can be:
A) Single T
B) List of T
C) Set of T
D) Map of String to T

Input:
- matcher - to be matched Matcher. Supports `Constant Matcher`, `Variable Matcher`, 
- parameters - input Parameters
- targets - to be matched container of targets

Output:
- status - 'matched' only if one or more target container items matched with `matcher`. 'not matched' otherwise.
- (if status==matched) parameters  - matched Parameters
- (if status==matched) remainingTargets - container of remaining targets which did not matched - these which has to be matched next    

MA-3) matching of container of targets with container of Matchers
Target can be:
A) Single T
B) List of T
C) Set of T
D) Map of String to T
Input:
- matchers - container of to be matched Matchers - M, List of M, Set of M, Map of M to M, which has to match to container of targets, Where M is a Matcher
- parameters - input Parameters
- targets - to be matched container of targets
- mode - `matchAll` ALL items of target container must match with ALL `matchers`, `matchSubset` if SUBSET of items of target container must match with ALL `matchers`
Output:
- status - 'matched' only if ALL/SUBSET of target container items matched with all `matchers`. 'not matched' otherwise.
- (if status==matched) parameters - matched Parameters
- (if status==matched && mode==matchSubset) remaintargets - container of targets with subset of items which did not matched
- (if status==matched) matchedTargets - container of targets with subset of items which did matched


Primitive Matchers
-----------------
**Constant Matcher**
Match: matches equal value (String, Enum, CtElement or List/Set/Map of previous). Is implemented as that value
Generate: generates copy of template value

**Variable Matcher**
matches any value (String, Enum, CtElement or List/Set/Map of previous) to the zero, one or more Parameters of type String, Enum, CtElement or List/Set/Map of previous.
Matcher has these options:
- mandatory - true if this Matcher must match in current state. false - Matching algorithm ignores this Matcher when there is no (more) match).
- repeatable - true if it may match again in current state (Matching algorithm tries to match this Matcher repeatedly until there is no match). false if can match maximum once.

Compound matchers
-----------------
consists of Primitive Matchers or Compound matchers. They are always implemented as a ordered chain of matchers.
The matching algorithm evaluates first Matcher from the chain and then remaining matchers

**XORMatcher** 
Contains ordered List of Matchers, which are evaluated sequentially until first Matcher matches. Others are ignored

**Container matcher**
Contains List, Set or Map of Matchers, which have to all match with provided targets

Wrapping matchers
-----------------
**Optional matcher**
Consists of a condition and a Matcher. 
If the Matcher matches then Parameters are set by the way the condition is true,
If the Matcher doesn't match, then Condition is set to false.'

**Nested matcher**
Consists of parameter mapping and Matcher. *
All the matched parameters are collected in local Parameters, which are then mapped to outer Parameters.

