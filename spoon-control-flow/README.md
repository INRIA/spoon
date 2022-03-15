# Spoon Control-flow

This module allow to build both the controlflow and the dataflow graphs of a java program based on its spoon AST.

spoon-control-flow is not currently deployed on Maven Central. It can be used by building from source. ```cd``` into the spoon-control-flow directory, and execute:
```
mvn package
```
This will generate the JAR files in the target directory

### Control flow

Classes `ControlFlowGraph` and `ControlFlowBuilder` can be used to build the control flow graph from a `CtElement` like shown in the example below:

```java

    ControlFlowBuilder builder = new ControlFlowBuilder();
    ControlFlowGraph graph = builder.build(element);

```

A use case is shown in `AllBranchesReturn`.

### Data flow graph

See `InitializedVariables` for a usecase.

### Exception control flow

Limited support for exception control flow is available using a strategy pattern:

```java

    ControlFlowBuilder builder = new ControlFlowBuilder();
    builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy());

    ControlFlowGraph graph = builder.build(element);

```

`NaiveExceptionControlFlowStrategy` models exception control flow under the following simplifying
assumptions:

1. Any statement can potentially throw any exception
2. All exceptions thrown inside a try block are caught by the catchers immediately associated with
the block.

Additionally, support for finalizers is limited by the lack of modeling for the semantics of return
statements in regards to executing finalizers before actually returning. Because of this limitation,
by default the implementation will refuse to model the flow of a try-(catch-)finally construct that
contains return statements. An option is available to allow the model to produce a partially 
incorrect graph where return statements jump directly to the exit without executing finalizers:

```java

    ControlFlowBuilder builder = new ControlFlowBuilder();

    EnumSet<NaiveExceptionControlFlowStrategy.Options> options;
    options = EnumSet.of(NaiveExceptionControlFlowStrategy.Options.ReturnWithoutFinalizers);

    builder.setExceptionControlFlowStrategy(new NaiveExceptionControlFlowStrategy(options));

    ControlFlowGraph graph = builder.build(element);

```
