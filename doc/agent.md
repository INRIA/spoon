---
title: Agent
tags: [usage]
keywords: agent, usage, java, loadtime
---

# Spoon Agent

Spoon can also be used at load time to transform classes. `SpoonClassFileTransformer` provide an abstraction of `ClassFileTransformer`
where the user can define its transformation with spoon instead of working directly on bytecode.
Bytecode of classes will be decompiled gradually when loaded, and the model will be updated in consequence.

The following example show the definition of a basic agent inserting a trace a the end of every method called `main`.

Here is the agent:
```java
public class Agent {
	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println( "Hello Agent" );

		//Create a SpoonClassFileTransformer, that
		// * excludes any classes not in our package from decompilation
		// * adds the statement System.out.println("Hello <className>"); to the (first) method main of every classes
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
				type.getMethodsByName("main").size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void transform(CtType type) {
		System.err.println("Transforming " + type.getQualifiedName());
		CtMethod main = (CtMethod) type.getMethodsByName("main").get(0);
		main.getBody().addStatement(type.getFactory().createCodeSnippetStatement("System.out.println(\"Hello " + type.getQualifiedName() + "\");"));
		System.err.println("Done transforming " + type.getQualifiedName());
	}
}
```