---
title: Create elements with factories
tags: [meta-model]
keywords: factories, elements, ast, meta, model
---

When you design and implement transformations, with processors 
or templates, you need to create new elements, fill their data and add 
them in the AST built by Spoon.

To do that, use `Factory` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/factory/Factory.html)). 
`Factory` is the entry point for all factories of Spoon. Each factory 
have a goal specific and help you in the creation of a new AST.

- `CoreFactory` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/factory/CoreFactory.html)) 
allows the creation of any element in the meta model. To set up the objects, there are setters to initialize the object.
- `CodeFactory` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/factory/CodeFactory.html)) 
contains utility methods to create code elements and asks minimal information
to create a valid object.
- `PackageFactory` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/factory/PackageFactory.html)) 
contains utility methods to create and get package reference.
- `TypeFactory` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/factory/TypeFactory.html)) 
contains utility methods with a link to `CtType` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/declaration/CtType.html)). 
You can get any type from its fully qualified name or a .class invocation  
and create all typed references like `CtTypeReference` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/reference/CtTypeReference.html)).
- `ClassFactory` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/factory/ClassFactory.html)) 
is a sub class of `TypeFactory` but specialized for `CtClass` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/declaration/CtClass.html)).
- `EnumFactory` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/factory/EnumFactory.html)) 
is a sub class of `TypeFactory` but specialized for `CtEnum` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/declaration/CtEnum.html)).
- `InterfaceFactory` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/factory/InterfaceFactory.html)) 
is a sub class of `TypeFactory` but specialized for `CtInterface` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/declaration/CtInterface.html)).
- `ExecutableFactory` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/factory/ExecutableFactory.html)) 
contains utility methods with a link to `CtExecutable` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/declaration/CtExecutable.html)). 
You can create executable objects and their parameters.
- `ConstructorFactory` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/factory/ConstructorFactory.html)) 
is a sub class of `ExecutableFactory` but specialized for `CtConstructor` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/declaration/CtConstructor.html)).
- `MethodFactory` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/factory/MethodFactory.html)) 
is a sub class of `ExecutableFactory` but specialized for `CtMethod` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/declaration/CtMethod.html)).
- `FieldFactory` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/factory/FieldFactory.html)) 
contains utility methods to create a valid field or a field reference.
- `AnnotationFactory` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/factory/AnnotationFactory.html)) 
contains utility methods to annotate any elements or create a new one.

All these factories contribute to facilitate the creation of elements. 
When you have created an element from a factory, set it in an existing element 
to build a new AST.

## SpoonifierVisitor

It is possible to visit an existing Spoon AST to generate calls to the factory that recreates the same AST.

Example:

```java
    SpoonifierVisitor v = new SpoonifierVisitor(true);
    Launcher.parseClass("class A { public String sayHello() { return \"Hello World!\";}}")
            .getMethodsByName("sayHello")
            .get(0)
            .accept(v);
    System.out.println(b.getResult());
```

will print:

```java
	CtMethod ctMethod0 = factory.createMethod();
	ctMethod0.setSimpleName("sayHello");
	Set<ModifierKind> ctMethod0Modifiers = new HashSet<>();
	ctMethod0Modifiers.add(ModifierKind.PUBLIC);
	ctMethod0.setModifiers(ctMethod0Modifiers);
		CtTypeReference ctTypeReference0 = factory.createTypeReference();
		ctTypeReference0.setSimpleName("String");
		ctMethod0.setValueByRole(CtRole.TYPE, ctTypeReference0);
			CtPackageReference ctPackageReference0 = factory.createPackageReference();
			ctPackageReference0.setSimpleName("java.lang");
			ctPackageReference0.setImplicit(true);
			ctTypeReference0.setValueByRole(CtRole.PACKAGE_REF, ctPackageReference0);
		CtBlock ctBlock0 = factory.createBlock();
		ctMethod0.setValueByRole(CtRole.BODY, ctBlock0);
			CtReturn ctReturn0 = factory.createReturn();
			List ctBlock0Statements = new ArrayList();
			ctBlock0Statements.add(ctReturn0);
				CtLiteral ctLiteral0 = factory.createLiteral();
				ctLiteral0.setValue("Hello World!");
				ctReturn0.setValueByRole(CtRole.EXPRESSION, ctLiteral0);
					CtTypeReference ctTypeReference1 = factory.createTypeReference();
					ctTypeReference1.setSimpleName("String");
					ctLiteral0.setValueByRole(CtRole.TYPE, ctTypeReference1);
						CtPackageReference ctPackageReference1 = factory.createPackageReference();
						ctPackageReference1.setSimpleName("java.lang");
						ctTypeReference1.setValueByRole(CtRole.PACKAGE_REF, ctPackageReference1);
		ctBlock0.setValueByRole(CtRole.STATEMENT, ctBlock0Statements);

```