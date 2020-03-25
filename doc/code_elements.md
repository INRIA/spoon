---
title: Code elements
tags: [meta-model]
keywords: code, elements, ast, meta, model
---

Figure at the end of this page shows the meta model for Java executable code. 
There are two main kinds of code elements. 
First, statements `CtStatement` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtStatement.html)) 
are untyped top-level instructions that can be used directly in a block of code. 
Second, expressions `CtExpression` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtExpression.html)) 
are used inside the statements. For instance, a `CtLoop` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtLoop.html)) 
(which is a statement) points to `CtExpression` which expresses its boolean condition.

Some code elements such as invocations and assignments are both statements 
and expressions (multiple inheritance links). Concretely, this is translated as an 
interface `CtInvocation` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtInvocation.html)) 
inheriting from both interfaces `CtStatement` and `CtExpression`. 
The generic type of `CtExpression` is used to add static type-checking when transforming programs.

![Code part of the Spoon Java 8 metamodel]({{ "/images/code-elements.png" | prepend: site.baseurl }})

### CtArrayRead
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtArrayRead.html)

```java

    int[] array = new int[10];
    System.out.println(
    array[0] // <-- array read
    );

```
### CtArrayWrite
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtArrayWrite.html)

```java

    Object[] array = new Object[10];
    // array write
    array[0] = "new value";

```
### CtAssert
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtAssert.html)

```java
assert 1+1==2
```
### CtAssignment
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtAssignment.html)

```java

    int x;
    x = 4; // <-- an assignment

```
### CtBinaryOperator
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtBinaryOperator.html)

```java

    // 3+4 is the binary expression
    int x = 3 + 4;

```
### CtBlock
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtBlock.html)

```java

 { // <-- block start
  System.out.println("foo");
 }
	
```
### CtBreak
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtBreak.html)

```java

    for(int i=0; i<10; i++) {
        if (i>3) {
				break; // <-- break statement
        }
    }

```
### CtCase
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtCase.html)

```java

int x = 0;
switch(x) {
    case 1: // <-- case statement
      System.out.println("foo");
}
```
### CtConditional
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtConditional.html)

```java

    System.out.println(
       1==0 ? "foo" : "bar" // <-- ternary conditional
    );

```
### CtConstructorCall
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtConstructorCall.html)

```java

    new Object();

```
### CtContinue
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtContinue.html)

```java

    for(int i=0; i<10; i++) {
        if (i>3) {
				continue; // <-- continue statement
        }
    }

```
### CtDo
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtDo.html)

```java

    int x = 0;
    do {
        x=x+1;
    } while (x<10);

```
### CtExecutableReferenceExpression
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtExecutableReferenceExpression.html)

```java

    java.util.function.Supplier p =
      Object::new;

```
### CtFieldRead
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtFieldRead.html)

```java

    class Foo { int field; }
    Foo x = new Foo();
    System.out.println(x.field);

```
### CtFieldWrite
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtFieldWrite.html)

```java

    class Foo { int field; }
    Foo x = new Foo();
    x.field = 0;

```
### CtFor
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtFor.html)

```java

    // a for statement
    for(int i=0; i<10; i++) {
    	System.out.println("foo");
    }

```
### CtForEach
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtForEach.html)

```java

    java.util.List l = new java.util.ArrayList();
    for(Object o : l) { // <-- foreach loop
    	System.out.println(o);
    }

```
### CtIf
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtIf.html)

```java

    if (1==0) {
    	System.out.println("foo");
    } else {
    	System.out.println("bar");
    }

```
### CtInvocation
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtInvocation.html)

```java

    // invocation of method println
    // the target is "System.out"
    System.out.println("foo");

```
### CtJavaDoc
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtJavaDoc.html)

```java

/**
 * Description
 * @tag a tag in the javadoc
*/

```
### CtLambda
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtLambda.html)

```java

    java.util.List l = new java.util.ArrayList();
    l.stream().map(
      x -> { return x.toString(); } // a lambda
    );

```
### CtLiteral
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtLiteral.html)

```java

    int x = 4; // 4 is a literal

```
### CtLocalVariable
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtLocalVariable.html)

```java

    // defines a local variable x
    int x = 0;


    // local variable in Java 10
    var x = 0;

```
### CtNewArray
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtNewArray.html)

```java

    // inline creation of array content
    int[] x = new int[] { 0, 1, 42}

```
### CtNewClass
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtNewClass.html)

```java

   // an anonymous class creation
   Runnable r = new Runnable() {
    	@Override
    	public void run() {
    	  System.out.println("foo");
    	}
   };

```
### CtOperatorAssignment
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtOperatorAssignment.html)

```java

    int x = 0;
    x *= 3; // <-- a CtOperatorAssignment

```
### CtReturn
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtReturn.html)

```java

   Runnable r = new Runnable() {
    	@Override
    	public void run() {
    	  return; // <-- CtReturn statement
    	}
   };

```
### CtSuperAccess
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtSuperAccess.html)

```java

    class Foo { int foo() { return 42;}};
    class Bar extends Foo {
    int foo() {
      return super.foo(); // <-- access to super
    }
    };

```
### CtSwitch
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtSwitch.html)

```java

int x = 0;
switch(x) { // <-- switch statement
    case 1:
      System.out.println("foo");
}
```
### CtSwitchExpression
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtSwitchExpression.html)

```java

int i = 0;
int x = switch(i) { // <-- switch expression
    case 1 -> 10;
    case 2 -> 20;
    default -> 30;
};
```
### CtSynchronized
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtSynchronized.html)

```java

   java.util.List l = new java.util.ArrayList();
   synchronized(l) {
    	System.out.println("foo");
   }

```
### CtThisAccess
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtThisAccess.html)

```java

    class Foo {
    int value = 42;
    int foo() {
      return this.value; // <-- access to this
    }
    };


```
### CtThrow
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtThrow.html)

```java

    throw new RuntimeException("oops")

```
### CtTry
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtTry.html)

```java

    try {
    	System.out.println("foo");
    } catch (Exception ignore) {}

```
### CtTryWithResource
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtTryWithResource.html)

```java

   // br is the resource
   try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("/foo"))) {
   	br.readLine();
  }

```
### CtTypeAccess
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtTypeAccess.html)

```java

    // access to static field
    java.io.PrintStream ps = System.out;


    // call to static method
    Class.forName("Foo")


    // method reference
    java.util.function.Supplier p =
      Object::new;


    // instanceof test
    boolean x = new Object() instanceof Integer // Integer is represented as an access to type Integer


    // fake field "class"
    Class x = Number.class

```
### CtUnaryOperator
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtUnaryOperator.html)

```java

    int x=3;
    --x; // <-- unary --

```
### CtVariableRead
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtVariableRead.html)

```java

    String variable = "";
    System.out.println(
      variable // <-- a variable read
    );

```
### CtVariableWrite
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtVariableWrite.html)

```java

    String variable = "";
    variable = "new value"; // variable write


    String variable = "";
    variable += "";

```
### CtWhile
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtWhile.html)

```java

    int x = 0;
    while (x!=10) {
        x=x+1;
    };

```
### CtYieldStatement
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtYieldStatement.html)

```java

    int x = 0;
    x = switch ("foo") {
        default -> {
					x=x+1;
					yield x; //<--- yield statement
					}
    };


    int x = 0;
    x = switch ("foo") {
        default -> 4; //<---  implicit yield statement
    };

```
### CtAnnotation
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/declaration/CtAnnotation.html)

```java

    // statement annotated by annotation @SuppressWarnings
    @SuppressWarnings("unchecked")
    java.util.List<?> x = new java.util.ArrayList<>()

```
### CtClass
[(javadoc)](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/declaration/CtClass.html)

```java

    // a class definition
    class Foo {
       int x;
    }

```
