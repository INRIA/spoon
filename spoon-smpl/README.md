# spoon-smpl

This module aims to implement support for (a variant/subset of) [SmPL](https://en.wikipedia.org/wiki/Coccinelle_(software)#Semantic_Patch_Language "Wikipedia entry").

Papers:
* [SmPL: A Domain-Specific Language for Specifying Collateral Evolutions in Linux Device Drivers](http://coccinelle.lip6.fr/papers/ercim.pdf)
* [A Foundation for Flow-Based Program Matching Using Temporal Logic and Model Checking](http://coccinelle.lip6.fr/papers/popl09.pdf)
* [Semantic Patches for Java Program Transformation](https://drops.dagstuhl.de/opus/volltexte/2019/10814/pdf/LIPIcs-ECOOP-2019-22.pdf)

## Architecture Notes

* Parsing SMPL: see SmPLLexer
    *  SmPLLexer is used by SmPLParser, whose main method is method `parse`. Next, method `rewrite` transforms the SmPL code into the Java DSL.
* class `SmPLRule` encapsulates a single rule, it is created by method `SmPLParser#compile
* class `FormulaCompiler` generates the CTL formula, method `compileFormula()` returns a `Formula`
* `ModelChecker` takes a `CFGModel` as input (there is one `CFGModel` instance per method in the project to be analyzed) (there are many `CFGModel` and only one `Formula), and then evaluates the formula on the model. The evaluation is done through interpretation with a visitor over the formula code. It returns a `Result`, which is taken as input by `Transformer`.
    

