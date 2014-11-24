Mission
======

The mission of Spoon is to provide a high-quality library for analyzing and transforming Java source code.

Design Philosophy
=================

R1) The Spoon metamodel is as close as possible to the language concepts.

R2) The Spoon model of a program is complete and sound.

R3) The text version of a Spoon model is well-formed and semantically equivalent to the original program.

R4) The analysis and transformation API is intuitive and regular.

R5) Transformation operators are designed to warn as fast as possible about invalid programs. This is done either with static type checking or with dynamic checks when the operators are used.

R6) When feasible, the text version of a Spoon model is close to the original one.


Future Work
==========

The future development of Spoon includes:

* support of Java 7
* support of Java 8
* testing framework for transformations
* keep original indentation for automated maintenance

 