---
title: Using Spoon as a Better Runtime Reflection API
---

Spoon can be used as an alternative to the standard Java reflection API.
Instead of manipulating a `java.lang.Class` object, you manipulate `CtClass` objects (also "shadow classes" of the normal binary classes).
The advantage is that you can share code for analyzing both binary and source code.

To do this, use `TypeFactory` as follows:

```java
CtType s = new TypeFactory().get(String.class);
System.out.println(s.getSimpleName());
```

Spoon also provides you with reflection over the Spoon metamodel itself, in class `spoon.Metamodel`:

```java
Set<CtType> list = Metamodel.getAllMetamodelInterfaces();
for(CtType t : l) {
    System.out.println(t.getMethods());
}
```

If you need to analyze the method bodies of binary code, have a look at [spoon-decompiler](https://github.com/INRIA/spoon/tree/master/spoon-decompiler).
