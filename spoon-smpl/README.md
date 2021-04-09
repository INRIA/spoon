# Spoon-smpl: Semantic Patches for Java

This Spoon module implements the Semantic Patch Language called [SmPL](https://en.wikipedia.org/wiki/Coccinelle_(software)#Semantic_Patch_Language "Wikipedia entry"). The Semantic Patch Language, invented for C by the seminal tool [Coccinelle](https://github.com/coccinelle/coccinelle) enables to apply transformations in an automated way, where the transformation itself is a patch written in a special syntax.

Here is an example  of a semantic patch that replaces all calls to a specific method by another one.

```java
@@
Context ctx;
expression E;
@@
- ctx.getResources().getColor(E)
+ ContextCompat.getColor(ctx , E)
``` 

Spoon-SmPL's initial version has been implemented by Mikael Forsberg as part of his master's thesis ["Design and Implementation of Semantic Patch Support for the Spoon Java Transformation Engine' at KTH Royal Institute of Technology"](http://urn.kb.se/resolve?urn=urn:nbn:se:kth:diva-291226).

References:
* [SmPL: A Domain-Specific Language for Specifying Collateral Evolutions in Linux Device Drivers](http://coccinelle.lip6.fr/papers/ercim.pdf)
* [A Foundation for Flow-Based Program Matching Using Temporal Logic and Model Checking](http://coccinelle.lip6.fr/papers/popl09.pdf)
* [Semantic Patches for Java Program Transformation](https://drops.dagstuhl.de/opus/volltexte/2019/10814/pdf/LIPIcs-ECOOP-2019-22.pdf)
* [Design and Implementation of Semantic Patch Support for the Spoon Java Transformation Engine' at KTH Royal Institute of Technology](http://urn.kb.se/resolve?urn=urn:nbn:se:kth:diva-291226)

## Supported SmPL Features

The following table shows some of the currently supported SmPL syntax/features:

| Syntax                | Description                                             |
|-----------------------|---------------------------------------------------------|
| `@@ identifier x; @@` | Identifier metavariable.                                |
| `@@ type x; @@`       | Arbitrary type name metavariable.                       |
| `@@ SomeType x; @@`   | Specific type name metavariable.                        |
| `@@ constant x; @@`   | Constant metavariable.                                  |
| `@@ expression x; @@` | Expression metavariable.                                |
| `  statement`         | Context match.                                          |
| `- statement`         | Match and deletion.                                     |
| `+ statement`         | Addition.                                               |
| `...`                 | Computation path wildcard operator.                     |
| `... when != expr`    | &nbsp;&nbsp; " &nbsp;&nbsp; with match constraint.      |
| `... when exists`     | &nbsp;&nbsp; " &nbsp;&nbsp; with constraint relaxation. |
| `... when any`        | &nbsp;&nbsp; " &nbsp;&nbsp; with constraint relaxation. |
| `<... P ...>`         | Computation path wildcard with optional match of `P`.   |
| `  f(...)`            | Argument or parameter list wildcard match.              |

Additional helpful resources currently available are:

1. [smpl_grammar.txt](https://github.com/INRIA/spoon/blob/master/spoon-smpl/smpl_grammar.txt): covers parts of the currently supported grammar.
2. [Test cases](https://github.com/INRIA/spoon/tree/master/spoon-smpl/src/test/resources/endtoend): contains easy-to-read test cases that reveal both supported patch language features and supported Java elements.
3. [PatternBuilder.java](https://github.com/INRIA/spoon/blob/master/spoon-smpl/src/main/java/spoon/smpl/pattern/PatternBuilder.java): shows the set of matchable Java elements, but can be cumbersome to read.
4. [Substitutor.java](https://github.com/INRIA/spoon/blob/master/spoon-smpl/src/main/java/spoon/smpl/Substitutor.java): shows the set of transformable Java elements, but can be cumbersome to read.

## Architecture Notes

* Parsing SMPL: see SmPLLexer
    *  SmPLLexer is used by SmPLParser, whose main method is method `parse`. Next, method `rewrite` transforms the SmPL code into the Java DSL.
* class `SmPLRule` encapsulates a single rule, it is created by method `SmPLParser#compile`
* class `FormulaCompiler` generates the CTL formula, method `compileFormula()` returns a `Formula`
* `ModelChecker` takes a `CFGModel` as input (there is one `CFGModel` instance per method in the project to be analyzed) (there are many `CFGModel` and only one `Formula`, and then evaluates the formula on the model. The evaluation is done through interpretation with a visitor over the formula code. It returns a `Result` containing a set of `Witness` that encode the metavariable bindings and transformation operations, and this set is taken as input by `Transformer`.
* `Transformer` takes the CFG model and a set of `Witness`
* package `spoon.smpl.operation` contains reified operations and a way to execute them
* `Substitutor` substitutes meta-variables by their bound values
* The full loop can be seen in method `spoon.smpl.SmPLProcessor#tryApplyPatch`

## Ideas

* CFGModel could be merged SmPLMethodCFG
* The Operation hierarchy could be merged with that of Gumtree-Spoon
* Substitutor could be unified with the existing Pattern architecture. 
