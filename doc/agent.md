---
title: Agent
tags: [usage]
keywords: agent, usage, java, loadtime
---

# Spoon Agent

Spoon can also be used to transform classes at load time in the JVM. FOr this, `SpoonClassFileTransformer` provide an abstraction of `ClassFileTransformer`
where the user can define Spoon transformation.
Bytecode of classes will be decompiled on-the-fly when loaded, and the Spoon AST will be updated in consequence, and the code is recompiled on-the-fly.

The following example shows the definition of a basic JVM agent for inserting a tracing method call a the end of every method called `foo`.

Here is the agent:
```java
public class Agent {
	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println( "Hello Agent" );

		//Create a SpoonClassFileTransformer, that
		// * excludes any classes not in our package from decompilation
		// * adds the statement System.out.println("Hello <className>"); to the (first) method named "foo" of every classes
        SpoonClassFileTransformer transformer = new SpoonClassFileTransformer(
                cl -> cl.startsWith("org/my/package"),
                new InsertPrintTransformer()
        );
		inst.addTransformer(transformer);

		System.out.println( "Agent Done." );
	}
}
```

```java
public class InsertPrintTransformer implements TypeTransformer {

	@Override
	public boolean accept(CtType type) {
		if ((type instanceof CtClass) &&
				type.getMethodsByName("foo").size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void transform(CtType type) {
		System.err.println("Transforming " + type.getQualifiedName());
		CtMethod main = (CtMethod) type.getMethodsByName("foo").get(0);
		main.getBody().addStatement(type.getFactory().createCodeSnippetStatement("System.out.println(\"Hello " + type.getQualifiedName() + "\");"));
		System.err.println("Done transforming " + type.getQualifiedName());
	}
}
```
:warning: The `SpoonClassFileTransformer` feature (and all features relying on decompilation) are not included in `spoon-core` but in `spoon-decompiler`. If you want to use them you should declare a dependency to `spoon-decompiler`.
