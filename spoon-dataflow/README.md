# spoon-dataflow

spoon-dataflow is a Z3 solver based data-flow analyzer for Java source code.

## Capabilities
At the moment spoon-dataflow has checkers which allow to detect logical errors (always true/false expressions) and null pointer dereferences.
For example:
```java
void f(boolean c) {
    int x = 5;
    int y = c ? 3 : 2;
    if (x < y) {} // <= always false
}

void g(int x, int y) {
    if (2 * x + 3 * y == 12) {
        if (5 * x - 2 * y == 11) {
            if (x == 3) {} // <= always true
            if (y == 2) {} // <= always true
        }
    }
}

void h(Something x) {
    if (x == null) {
        x.f(); // <= null dereference
    }
}
```
Check out test directory for more examples.

In general, spoon-dataflow is capable to evaluate expressions statically, perform symbolic execution, handle control flow of a program, and so on. It also features a proper memory model, so it nicely deals with reference aliasing.

## Build and run
In order to build spoon-dataflow you need JDK 8 or newer. Also, you have to download and install Z3.

### Windows:
1. Download z3-4.8.4 here: https://github.com/Z3Prover/z3/releases
2. Add bin directory to your PATH environment variable
3. Run `gradlew build`

Note: Visual C++ 2015 Redistributable may be required.

### Linux:
1. Download z3-4.8.4 here https://github.com/Z3Prover/z3/releases
2. Add bin directory to your LD_LIBRARY_PATH: `export LD_LIBRARY_PATH=/path/to/z3/bin`
3. Run `./gradlew build`

### macOS:
1. Download z3-4.8.4 here https://github.com/Z3Prover/z3/releases
2. Add bin directory to your DYLD_LIBRARY_PATH: `export DYLD_LIBRARY_PATH=/path/to/z3/bin`
3. Run `./gradlew build`

Note that you may still need to set up environment variables or java.lang.path in your IDE.

Now you can go to the build directory and run the resulting jar:    
`java -jar spoon-dataflow.jar -sources <arg...> [-classpath <arg...>]`

## Under the hood
### AST
spoon-dataflow uses Spoon library to build and traverse AST in a post-order manner.
While traversing the AST, it translates the source code into a form understandable by Z3 solver. Then the checks are performed by the solver.

### Static single assignment form (SSA)
First of all, in order to be used with Z3, the code should be present in [Static Single Assignment form](https://en.wikipedia.org/wiki/Static_single_assignment_form). We do this to maintain the order of a program in a logic formula.
Here is a simple example:

Original form:
```
x = 1;
y = 2;
x = x + y;
```
SSA form:
```
x0 = 1;
y0 = 2;
x1 = x0 + y0;
```

### Conditions
After traversing if statement, we create a new variable, which is the result of a special if-then-else function provided by Z3. This function essentially joins values from branches over a condition.

Original form:
```
if (c) {
    x = 1;
} else {
    x = 2;
}
```
SSA form:
```
if (c) {
    x0 = 1;
} else {
    x1 = 2;
}
x3 = ITE(c0, x0, x1);
```

### Functions and interprocedural analysis
The most obvious approach to deal with function calls is to inline them. Unfortunately, we can do it only for small functions (like getters or setters), because of the performance considerations.   
Another approach is to build Function Summaries (automatically or manually), which contain information about contracts, purity, return values and so on.   
But in general, any unknown function resets the values of its arguments.

### Loops
The most obvious approach to deal with loops is to unroll them. Unfortunately, we can do it only when the number of iterations is known statically and relatively small.   
So in general, we have to find loop invariants and reset everything else, like for the functions.   
However, some special cases could be treated differently.

### Memory model
spoon-dataflow uses type-indexed memory model (Burstall’s memory model).
In this memory model each data type or field has its own memory array.
It allows to detect something like that:
```java
void m() {
    C a = new C();
    a.x = 10;
    C b = a;
    b.x = 20;
    if (a.x == 20) {} // <= always true
}
```
Here are some useful links with a more detailed explanation of different memory models:
- https://llvm.org/pubs/2009-01-VMCAI-ScalableMemoryModel.pdf
- https://www.researchgate.net/publication/221430855_A_Memory_Model_for_Static_Analysis_of_C_Programs


## To Do list
At the moment this project is just a proof of concept, so there is a lot of work to do:
- Interprocedural analysis and Function Summaries;
- Parallel analysis and classes dependency graph;
- Support for the remaining language constructions;
- Better loop analysis;
