---
title: Create elements with factories
tags: [meta-model]
keywords: factories, elements, ast, meta, model
last_updated: October 1, 2015
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

