

Spoon Roadmap
==========

Short-term, long term and crazy ideas about Spoon

version of 03/09/2015


* Model
    * support for analyzing bound vs unbound type references (`List<T>` vs `List<String>`) 
    * build model of binary code using a decompiler 
    * Improves usage of generics on the model (See https://github.com/INRIA/spoon/issues/583#issue-148728790)
    * Add an embedded DSL / builder mechanism (see https://github.com/INRIA/spoon/pull/741) 
* Transformations:
    * Keep original when printing after transformation
        * inline comments
        * indentation and formatting   
    * sniper mode (only rewrites the changed nodes)
    * Transactional transformations (rollback if transfo fails)
    * adds generic transformations: a generic transformation is a transformation that is independent of the domain and can be applied to any source code, their goal is to facilitate analysis and transformation
        * everything in a block
        * unfinalizer (remove as many "final" keywords as possible)
* Processor orchestration 
    * specify the current strategy
        * what if multiple processors on the same node
        * what if new nodes added?
    * improved version, see '[Source model analysis using the JJTraveler visitor combinator framework](http://www3.di.uminho.pt/~joost/publications/SourceModelAnalysisUsingTheJJTravelerVisitorCombinatorFramework.pdf)'    
    * dependency models between processors
* Spoon in the IDE
    * on the fly model update
    * lazy spoon (build the class model only after a call to getDeclaration)
* Child project spoon-refactorings (starting from TTC)
* Templates
    * templates as query language (see TemplateMatcher)
    * inline templates
        * with anonymous classes
        * with lambda
* Write spoon processors, compile them to ASM transformations on bytecode
 
