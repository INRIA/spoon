# Spoon-Architecture

Spoon-Architecture is a high level architecture checking framework. It allows enforcing architecture rules and simplifies review process because well known rules are formulated as test cases.

# Motivation

Spoon had a growing size in reusable architecture checks, but these architecture checks are complex to read for a non spoon developer. We wanted to create a framework to allow more developers/projects to write architecture checks. Easy to write, maintainable and writable without knowledge about java parsing architecture tests are provided with this. These architecture checks shall increase code quality in the long run and motivate more projects to check there architecture.
# Build status

[![Travis Build Status](https://travis-ci.com/INRIA/spoon.svg?branch=master)](https://travis-ci.com/INRIA/)


# Features

- Architecture check runner (like junit)
- AST abstraction of spoon
- Many predefined preconditions and constraints.
- Custom report printer and error handler

# API Reference

Soon online

# Tests

To run the test use maven:
``` java
maven test
```

# Get started in a view seconds

Create any ``ArchitectureTest`` e.g. all test methods start with ``test``.
```java
	@Architecture
	public void methodNameStartsWithTest(CtModel srcModel, CtModel testModel) {
		Precondition<CtMethod<?>> pre =	Precondition.of(DefaultElementFilter.METHODS.getFilter(), VisibilityFilter.isPublic(), AnnotationHelper.hasAnnotationMatcher(Test.class));
		Constraint<CtMethod<?>> con = Constraint.of(System.out::println, Naming.startsWith("test"));
		ArchitectureTest.of(pre, con).runCheck(testModel);
	}
```
and add 
```java
public static void main(String[] args) {
	  		SpoonArchitecturalChecker.createChecker().runChecks();
}
```
See [FirstArchitectureRule](https://github.com/INRIA/spoon/spoon-architecture/doc/FirstArchitectureRule.md) for a detailed explanation. 


# Contribute 

All contributions are welcome, see [contribute](https://github.com/INRIA/spoon/blob/master/CONTRIBUTING.md) from spoon-core for more information.

# Credits

Initial implementation is done by @MartinWitt with guidance by @monperrus and valuable inputs by @vmassol.
For all authors use ``git shortlog -sne`` 
# License



 