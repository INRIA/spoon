# Spoon Control-flow

This module allow to build both the controlflow and the dataflow graphs of a java program based on its spoon AST.

To use it with maven, add the following dependency to your pom.xml

```xml
    <dependency>
        <groupId>fr.inria.gforge.spoon</groupId>
        <artifactId>control-flow</artifactId>
        <version>0.0.1</version>
    </dependency>
```

### Control flow

Classes `ControlFlowGraph` and `ControlFlowBuilder` can be used to build the control flow graph from a `CtElement` like shown in the example below:

```java

    ControlFlowBuilder builder = new ControlFlowBuilder();
    ControlFlowGraph graph = builder.build(element);

```

A use case is shown in `AllBranchesReturn`.

### Data flow graph

See `InitializedVariables` for a usecase.