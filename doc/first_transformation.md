---
title: First transformation processor
tags: [getting-started]
keywords: start, begin, hello world, processor, spoon, factory, setter
---


## Goal


We'll make a first transformation that adds a field to a class
and initializes it in the constructor of the current class.


## Factories and setters

With `Factory` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/factory/Factory.html)), 
you can get and create all elements of the meta model. For example, if you want 
to create a class with the name "Tacos", use the factory to create an empty class 
and fill information on the created element to set its name.

```java
CtClass newClass = factory.Core().createClass();
newClass.setSimpleName("Tacos");
```

First, create a new field. To do that, create the type referenced by our field. 
This type is a `java.util.List` which have a `java.util.Date` as generic type.

```java
final CtTypeReference<Date> dateRef = getFactory().Code().createCtTypeReference(Date.class);
final CtTypeReference<List<Date>> listRef = getFactory().Code().createCtTypeReference(List.class);
listRef.addActualTypeArgument(dateRef);
```

`dateRef`, a `CtTypeReference` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/reference/CtTypeReference.html)), 
is created by our factory from `Date.class` given by Java. We also created `listRef` which 
is created by our factory from `List.class` and we add our `dateRef` as actual type argument 
which represents the generic type of the list.

Now, create the field. A field has a name, a type and private.

```java
final CtField<List<Date>> listOfDates = getFactory().Core().<List<Date>>createField();
listOfDates.setSimpleName("dates");
listOfDates.setType(listRef);
listOfDates.addModifier(ModifierKind.PRIVATE);
```

We have created a field named "dates", with a private visibility and typed by our previous type reference, 
`listRef`, which is `java.util.List<java.util.Date>`. 

Second, create the constructor. Before the creation of a `CtConstructor` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/declaration/CtConstructor.html)), 
create all objects necessary for this constructor and set them in the target constructor. 
The constructor has a parameter typed by the same type of the field previously created 
and has a body to assign the parameter to the field.

```java
final CtCodeSnippetStatement statementInConstructor = getFactory().Code().createCodeSnippetStatement("this.dates = dates");

final CtBlock<?> ctBlockOfConstructor = getFactory().Code().createCtBlock(statementInConstructor);

final CtParameter<List<Date>> parameter = getFactory().Core().<List<Date>>createParameter();
parameter.setType(listRef);
parameter.setSimpleName("dates");

final CtConstructor constructor = getFactory().Core().createConstructor();
constructor.setBody(ctBlockOfConstructor);
constructor.setParameters(Collections.<CtParameter<?>>singletonList(parameter));
constructor.addModifier(ModifierKind.PUBLIC);
```

*Wow! Wait ... What is `CtCodeSnippetStatement`?*

You can convert any string in a `CtStatement` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtStatement.html)) 
with `createCodeSnippetStatement(String statement)` or in `CtExpression` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtExpression.html)) 
with `createCodeSnippetExpression(String expression)`. In our case, we convert `this.dates = dates` 
in a `CtAssignement` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtAssignment.html)) 
with an assignment and an assigned elements.

With this last example, you have created a statement that you have put in a block. 
You have created a parameter typed by the same type as the field and 
you have put all these objects in the constructor.

Finally, apply all transformations in your processor:

```java
public class ClassProcessor extends AbstractProcessor<CtClass<?>> {
	@Override
	public void process(CtClass<?> ctClass) {
		// Creates field.
		final CtTypeReference<Date> dateRef = getFactory().Code().createCtTypeReference(Date.class);
		final CtTypeReference<List<Date>> listRef = getFactory().Code().createCtTypeReference(List.class);
		listRef.addActualTypeArgument(dateRef);
		final CtField<List<Date>> listOfDates = getFactory().Core().<List<Date>>createField();
		listOfDates.<CtField>setType(listRef);
		listOfDates.<CtField>addModifier(ModifierKind.PRIVATE);
		listOfDates.setSimpleName("dates");

		// Creates constructor.
		final CtCodeSnippetStatement statementInConstructor = getFactory().Code().createCodeSnippetStatement("this.dates = dates");
		final CtBlock<?> ctBlockOfConstructor = getFactory().Code().createCtBlock(statementInConstructor);
		final CtParameter<List<Date>> parameter = getFactory().Core().<List<Date>>createParameter();
		parameter.<CtParameter>setType(listRef);
		parameter.setSimpleName("dates");
		final CtConstructor constructor = getFactory().Core().createConstructor();
		constructor.setBody(ctBlockOfConstructor);
		constructor.setParameters(Collections.<CtParameter<?>>singletonList(parameter));
		constructor.addModifier(ModifierKind.PUBLIC);

		// Apply transformation.
		ctClass.addField(listOfDates);
		ctClass.addConstructor(constructor);
	}
}
```

## Refactoring transformations

Spoon provides some methods for automated refactoring:.

* [Local Variable Refactoring](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/refactoring/CtRenameLocalVariableRefactoring.html)
class, renames local variables and includes extra checking to ensure program correctness after renaming,
* [Generic Variable Refactoring](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/refactoring/CtRenameGenericVariableRefactoring.html)
class, renames any variable type (field, parameter, local), but does not do any extra checking to ensure program correctness.
* `Refactoring` contains helper methods for refactoring, incl. one for automated removal of deprecated methods.
