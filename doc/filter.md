---
title: Navigation and Query
tags: [quering]
keywords: quering, query, filter, ast, elements
---


Getters
-------

All elements provide a set of appropriate getters to get the children of an element.


```java
methods = ctClass.getMethods();
```

In addition, there exists a generic getter based on the role played by an element with respect to its parent. See CtRole for a complete list of roles.

```java
methods = ctClass.getValueByRole(CtRole.METHOD);
```

While not recommended, it is also possible to get all direct children of an element

```java
allDescendants = ctElement.getDirectChildren();
```


Filters
-------

A Filter defines a predicate of the form of a `matches` method that
returns `true` if an element has to be selected in the filtering operation.
A Filter is given as parameter to `CtElement#getElement(Filter)` ()or `CtQueryable#filterChildren(Filter)`) which implements a depth-first search algorithm. During AST traversal, the elements satisfying the matching predicate are selected by the filter.

Here are code examples about the usage of filters. The first example returns all AST nodes of type `CtAssignment`.

```java
// collecting all assignments of a method body
list1 = methodBody.getElements(new TypeFilter(CtAssignment.class));
```

The second example selects all deprecated classes.

```java
// collecting all deprecated classes
list2 = rootPackage.getElements(new AnnotationFilter(Deprecated.class));
```

Now let's consider a user-defined filter that only matches public fields across all classes.

```java
// creating a custom filter to select all public fields
list3 = rootPackage.filterChildren(
  new AbstractFilter<CtField>(CtField.class) {
    @Override
    public boolean matches(CtField field) {
      return field.getModifiers.contains(ModifierKind.PUBLIC);
    }
  }
).list();
```

Scanners
--------

`CtScanner` provides a simple way to visit a node and its children.

```java
//Scanner counting the number of CtFieldWrite
class CounterScanner extends CtScanner {
	private int visited = 0;
	@Override
	public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
		visited++;
	}
}

CounterScanner scanner = new CounterScanner();

//Run the scanner on an element, here the CtClass representing FieldAccessRes
launcher.getFactory().Class().get("FieldAccessRes").accept(scanner);

//scanner.visited now contains the number of children of type CtFieldWrite 
assertEquals(1, scanner.visited);
```

`EarlyTerminatingScanner` is a specialized Class implementing `CtScanner` that stops once `terminate()` has been called.

See also `CtVisitor`.

Iterator
--------

`CtIterator` provides an iterator on all transitive children of a node in depth first order.

```java
CtIterator iterator = new CtIterator(root);
while (iterator.hasNext()) {
	CtElement el = iterator.next();
	//do something on each child of root
}
```

`CtBFSIterator` is similar to CtIterator but in Breadth first order.

Queries
-------

The Query, introduced in Spoon 5.5 by Pavel Vojtechovsky, is an improved filter mechanism:

* queries can be done with a Java 8 lambda
* queries can be chained
* queries can be reused on multiple input elements

**Queries with Java8 lambdas**: `CtQueryable#map(CtFunction)`enables you to give Java 8 lambda as query.

```java
// returns a list of String
list = package.map((CtClass c) -> c.getSimpleName()).list();
```

**Compatibility with existing filters** `CtQueryable#filterChildren(Filter)` is a filtering query that can be chained:

```java
// collecting all methods of deprecated classes
list2 = rootPackage
    .filterChildren(new AnnotationFilter(Deprecated.class)).list()
```

A boolean return value of the lambda tells whether the elements are selected for inclusion or not.

```java
// creating a custom filter to select all public fields using java 8 lambda
list3 = rootPackage.filterChildren((CtField field)->field.getModifiers.contains(ModifierKind.PUBLIC)).list();
```

**Chaining** If the CtFunction returns an object, this object is given as result to the next step of the query.
All results of the last query step are results of the query. The results of intermediate steps are not results of the query.:

```java
// a query which processes all not deprecated methods of all deprecated classes
rootPackage
  .filterChildren((CtClass clazz)->clazz.getAnnotation(Deprecated.class)!=null)
  .map((CtClass clazz)->clazz.getMethods())
  .map((CtMethod<?> method)->method.getAnnotation(Deprecated.class)==null)
  .list()
;
```

Finally, if the CtFunction returns and Iterable or an Array then each item of the collection/array is sent to next query step or result.

**Query reuse**. Method `setInput` allows you to reuse the same query over multiple inputs. 
In such case it makes sense to create unbound query using `Factory#createQuery()`.

```java
// here is the query
CtQuery q = factory.createQuery().map((CtClass c) -> c.getSimpleName());
// using it on a first input
String s1 = q.setInput(cls).list().get(0);
// using it on a second input
String s2 = q.setInput(cls2).list().get(0);
```

**Query evaluation**. Each example above use `CtQuery#list()` to evaluate the query.
The `list` method evaluates the query and returns the `List`, which contains all the results of the query.  

But it is not the only way how to evaluate query. There is `CtQuery#forEach(CtConsumer)`, 
which sends each query result to the `CtConsumer#accept` function. 
It is more efficient in cases when query results can be immediately processed.

```java
//prints each deprecated element
rootPackage
    .filterChildren(new AnnotationFilter(Deprecated.class)).forEach((CtElement ele)->System.out.println(ele));
```

Finally there is `CtQuery#first()`, 
which evaluates the query until first query result is found.
Then the evaluation is terminated and first result is returned. 
It is faster then `query.list().get(0)`, because query engine does not collect other results.

```java
// returns first deprecated element
CtElement firstDeprecated = rootPackage.filterChildren(new AnnotationFilter(Deprecated.class)).first();
```
