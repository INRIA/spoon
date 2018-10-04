

Spoon Roadmap
==========

Short-term, long term and crazy ideas about Spoon

* Model
    * build model of binary code using a decompiler 
    * Improves usage of generics on the model (See https://github.com/INRIA/spoon/issues/583#issue-148728790)
    * Add an embedded DSL / builder mechanism (see https://github.com/INRIA/spoon/pull/741) 
* Transformations:
    * Transactional transformations (rollback if transfo fails)
    * adds generic transformations: a generic transformation is a transformation that is independent of the domain and can be applied to any source code, their goal is to facilitate analysis and transformation
        * unfinalizer (remove as many "final" keywords as possible)
* Processor orchestration 
    * specify the current strategy
        * what if multiple processors on the same node
        * what if new nodes added?
    * improved version, see '[Source model analysis using the JJTraveler visitor combinator framework](http://www3.di.uminho.pt/~joost/publications/SourceModelAnalysisUsingTheJJTravelerVisitorCombinatorFramework.pdf)'    
    * dependency models between processors
    * Write transformation processors with Spoon, compile them to ASM transformations on bytecode
* Spoon in the IDE
    * on the fly model update
    * lazy spoon (build the class model only after a call to getDeclaration)
* Templates
    * inline templates
        * with anonymous classes
        * with lambda
* Spoonifyier: give a snippet as input and produce as output the Spoon code that would create this snippet 
* From Github: see <https://github.com/INRIA/spoon/issues?q=is%3Aopen+is%3Aissue+label%3Afeature>
