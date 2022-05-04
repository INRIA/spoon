### Scope of implementation as of May 2022


#### SmPL Metavariables
|   Name/Description           |  Binds                                     | Example               |
| ----------------------- | ------------------------------------------ | --------------------- |
|    Constant             |  `CtLiteral`                               | `@@ constant C; @@`   |
|    Expression           |  `CtExpression`                            | `@@ expression E; @@` |
|    Identifier           |  `CtVariableReference`                     | `@@ identifier x; @@` |
|    Type                 |  `CtTypeReference`                         | `@@ type T; @@`       |
|    Typed identifier     |  `CtVariableReference` with correct type   | `@@ Integer x; @@`    |
|    Regex[^1]            |  Any[^1]                                   | `@@ expression E when matches "regex"; @@`    |

[^1]: The Regex metavariable can be applied as an extra constraint on top of any other metavariable
constraint (e.g identifier) and adds the additional constraint of the string representation (.toString)
of a candidate binding element having to match the given regex.

#### SmPL Patch syntax

Referenced tests are found at https://github.com/INRIA/spoon/tree/master/spoon-smpl/src/test/resources/endtoend

<table>
<tr>
<td> Description </td>
<td> Example </td>
<td> Test(s) </td>
</tr>

<tr>
<td> Prepend/append to matched context </td>
<td> 

```java
+ a();
  ctx();    // matched context
+ b();
```

 </td>
<td> AppendToContext, AppendContextBranch, PrependToContext, PrependContextBranch </td>
</tr>

<tr>
<td> Delete all matched </td>
<td> 

```java
- a();
```

 </td>
<td> TypedIdentifierMetavariables1 </td>
</tr>

<tr>
<td> Delete around matched context </td>
<td> 

```java
- a();
  ctx();
- b();
```

 </td>
<td> DeleteStmBeforeBranch, DeleteStmAfterBranch </td>
</tr>

<tr>
<td> Enclose matched context in branch </td>
<td> 

```java
+ if (cond) {
  ctx();
+ }
```

 </td>
<td> EncloseInBranch </td>
</tr>

<tr>
<td> Delete enclosing branch </td>
<td> 

```java
- if (cond) {
  ctx();
- }
```

 </td>
<td> DeleteEnclosingBranch </td>
</tr>

<tr>
<td> Dots (computation path wildcard) </td>
<td> 

```java
  int v1;
  ...
- v1 = C;
+ v1 = C + 1;
```

 </td>
<td> BasicDots, DotsShortestPath </td>
</tr>

<tr>
<td> Dots "when any" </td>
<td> 

```java
  int v1;
  ... when any
- v1 = C;
+ v1 = C + 1;
```

 </td>
<td> DotsWhenAny </td>
</tr>

<tr>
<td> Dots "when exists" </td>
<td> 

```java
  a();
  ... when exists
- c();
```

 </td>
<td> Exceptions/ExistsDotsPatchingCatchBlock </td>
</tr>

<tr>
<td> Dots "when != x" </td>
<td> 

```java
- a();
  ... when != b()
- c();
```

 </td>
<td> DotsWhenNeqExpression/* </td>
</tr>

<tr>
<td> "Optional match" dots <... P ...> </td>
<td> 

```java
  a();
<...
- b(x);
+ log(x);
...>
- c();
```

 </td>
<td> DotsWithOptionalMatch/* </td>
</tr>

<tr>
<td> Binding metavariables on method header </td>
<td> 

```java
@@ type T; @@
  T square(T x) {
-    log(...);
  }
```

 </td>
<td> MatchingMethodHeader/MethodHeaderBinding </td>
</tr>

<tr>
<td> Literal matching in method header + params </td>
<td> 

```java
  int square(int x) {
```

 </td>
<td> MatchingMethodHeader/MethodHeaderLiteralMatch </td>
</tr>

<tr>
<td> Wildcard matching in method header params </td>
<td> 

```java
  T fn(..., Point pt, ...) {
```

 </td>
<td> MatchingMethodHeader/MethodHeaderDots </td>
</tr>

<tr>
<td> Wildcard matching in method arguments </td>
<td> 

```java
- f(...);
- f(..., g(..., 1));
```

 </td>
<td> MethodCallArgDots/* </td>
</tr>

<tr>
<td> Add full method to class </td>
<td> 

```java
+ void b() {
+   System.out.println("Hello, World!");
+ }

void fn() {
  a();
+ b();
}
```

 </td>
<td> MethodsAddedToClass/* </td>
</tr>

<tr>
<td> Pattern disjunction w/ clause order priority </td>
<td> 

```java
(
- a();
|
- b();
|
- c();
)
```

 </td>
<td> BasicPatternDisjunction </td>
</tr>

</table>


#### Matchable code 

Java elements/constructs you can specify in the semantic patch in order to match against real code,
produce new code in the output, or produce transformed code in the output. Not all
aspects/features/attributes are necessarily supported.

|   Description                           |  Examples                             |
| --------------------------------------- | ------------------------------------- |
|    Literals                             |   `5`, `"Hello"`                      |
|    Type names                           |   `int`                               |
|    Variable declarations                |   `int x;`                            |
|    Assignments                          |   `int x = 5;`                        |
|    Variable/field references            |   `x`                                 |
|    Binary operators                     |   `+`, `-`                            |
|    Unary operators                      |   `++`, `--`                          |
|    Ternary expressions                  |   `expr ? then : else`                |
|    if-statements                        |   `if (cond) { ... } [else { ... }]`  |
|    Array types                          |   `int[]`                             |
|    Constructor invocations              |   `new Foo();`                        |
|    Constructor invocations w/ arguments |   `new Foo(1,2,3);`                   |
|    Method invocations                   |   `println();`                        |
|    Method invocations w/ arguments      |   `println("hello world");`           |
|    Method header w/ parameters          |   `void foo(int bar) { ... }`         |
|    Return statements                    |   `return x;`                         |
|    "this" access                        |   `this`                              |

#### Non-matchable code 

Specifying any of these in a patch will either lead to unsupported element warnings or exceptions.

Encountering any of these in target code can[^2] either lead to unsupported element warnings or
exceptions.

|   description                                      | examples                                                               |
| -------------------------------------------------- | ---------------------------------------------------------------------- |
|    Package declarations                            |  `package spoon.smpl;`                                                 |
|    Module declarations                             |                                                                        |
|    Imports                                         |  `import foo;`                                                         |
|    Comments, Javadoc                               |  `/* hello */`                                                         |
|    Annotations                                     |  `@Entity(tableName = "vehicles")`                                     |
|    Lambda expressions                              |  `(int x) -> x + 1;`                                                   |
|    Classes, interfaces, records, inner variants of |                                                                        |
|    Anonymous inner classes                         |  `Runnable f = new Runnable() { @override public void run() { ... }};` |
|    Instance initializers / anonymous blocks        |                                                                        |
|    Array read/write                                |  `x[4]`, `x[j] = 9`                                                    |
|    Array constructor                               |  `new int[42];`                                                        |
|    Method references                               |  `Class::method`                                                       |
|    Assertions                                      |  `assert cond;`                                                        |
|    Type parameters, intersection types             |  `Map<String,Integer> ...`                                             |
|    Switch, case, yield                             |                                                                        |
|    Break, continue                                 |                                                                        |
|    Throw statements                                |  `throw new Exception();`                                              |
|    Try, catch, finally                             |                                                                        |
|    Try-with-resource                               |  `try (BufferedReader br = new ...) { ... }`                           |
|    Enums                                           |                                                                        |
|    Loops: while, do, for, foreach                  |                                                                        |
|    Text blocks                                     |  `"""hello"""`                                                         |
|    Operator-assignments                            |  `+=`, `*= `                                                           |
|    Synchronized statements                         |  `synchronized(foo) { ... }`                                           |

[^2]: There is a prefiltering step when matching a semantic patch against target code. The filter
step skips over any executables that cannot possibly match the patch, based on the presence of
certain strings. Any executables that are not skipped over by the filter can be called candidate
executables. Encountering any of the constructs in the list of non-matchable code inside a candidate
executable will lead to unsupported element warnings or exceptions.

#### Matchable but non-producible code

A semantic patch specifying e.g the addition `+ E` for a non-producible `E` will either lead to
unsupported element warnings or exceptions.

|   Description           |     Examples                  |
| ----------------------- | ----------------------------- |
|    Array types          |    `int[]`                    |
|    Ternary expressions  |    `expr ? then : else`       |
