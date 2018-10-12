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

		//We exclude any classes not in our package from decompilation
		inst.addTransformer(new InsertTraceInMainTransformer(cl -> cl.startsWith("org/my/package")));

		System.out.println( "Agent Done." );
	}
}
```

Here is the class extending SpoonClassFileTransformer
```java
public class InsertTraceInMainTransformer extends SpoonClassFileTransformer {
	public TSpoonClassFileTransformer(Predicate<String> classNameFilter) {
		super(classNameFilter);
	}

	@Override
	//Accept any class for which we have decompiled bytecode but exclude interfaces
	public boolean accept(CtType type) {
		return (type instanceof CtClass);
	}

	@Override
	public void transform(CtType type) {
	    //Get main method
		CtMethod main = (CtMethod) type.getMethodsByName("main").get(0);
		//Insert a trace containing the name of the class
		main.getBody().addStatement(type.getFactory().createCodeSnippetStatement("System.out.println(\"Hello " + type.getQualifiedName() + "\");"));
	}
}
```