
### Your first Architecture-Rule

Lets start with the rule. The rule is "Every testmethod has the a name starting with 'test'".
 A rule is a method with the annotation `@Architecture`.  Both arguments are models from your source code. We look at them later. The method looks like this:
```java
@Architecture
public void testMethodNamesStartWithTest(CtModel srcModel, CtModel testModel) {
}
```
Lets look back at the rule. We want all methods that have the `@Test` and check if the methodname starts with `test`.
The precondition is all methods annotated with `@Test` and the constraint is the methodname starts with `test`.
A architecture rule consists of two parts:
- A preconditions specifying the elements, that must hold a property.
- A constraint describing a condition all elements must hold e.g. methodname starts with test.

#### Precondition

We already have predefined filters for faster start. We want all filter that gives us all methods.
```java
Precondition<CtMethod<?>> pre =	Precondition.of(
              DefaultElementFilter.METHODS.getFilter(),
              Visibility.PUBLIC,
              AnnotationHelper.hasAnnotationMatcher(Test.class));
```
You can ignore the CtMethod generic for now. We create a precondition with the static method `of` having 2 arguments. The first an element selector, here `DefaultElementFilter.METHODS.getFilter()` and the second argument is varargs of Predicates. These predicates help filtering the elements before checking the condition. We have 2 Predicates here, the first checks if the method is public. A junit testcase is always public. The second method checks if the method has the `@Test` annotation.
After we created the precondition lets look at the constraint.

#### Constraint
The constraint is created with the static `Constraint.of(IError, Predicate<?>...) `method. The `IError` is for reporting errors and for simplicity for now a simple print of the element. Our rule was that every testmethods name starts with test. In the precondition we filtered all test methods, so now we only need to check if the name starts with "test". For naming checks there is a class with predefined default methods. Naming.startsWith checks if a name starts with the given string. 
```java
		Constraint<CtNamedElement> con = Constraint.of(System.out::println, Naming.startsWith("test"));
```
#### ArchitectureTest
Now we have a the precondition and constraint and we only need to create the ArchitectureRule.
The rule is create by the static method `ArchitectureTest.of(Precondition<T>, Constraint<T>)`. The `runCheck(testModel);` tells the runner which model should be checked, here the testModel.
Thats simply `ArchitectureTest.of(pre, con).runCheck(testModel);`

#### Runner
Now you may ask yourself how can I run this test? We have a runner integrated. The following line integrates your architecture rule into junit and allows easy usage.
```java	
@Test
	public void allChecksMustRun() {
		SpoonArchitecturalCheckerImpl.createChecker().runChecks();
       }
```

#### Wrapping it up

The full example rule is the following:
````java
	@Architecture
	public void methodNameStartsWithTest(CtModel srcModel, CtModel testModel) {
		Precondition<CtMethod<?>> pre =	Precondition.of(DefaultElementFilter.METHODS.getFilter(), Visibility.PUBLIC, AnnotationHelper.hasAnnotationMatcher(Test.class));
		Constraint<CtMethod<?>> con = Constraint.of(System.out::println, Naming.startsWith("test"));
		ArchitectureTest.of(pre, con).runCheck(testModel);
	}
```
