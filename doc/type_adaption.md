# Type Adaption

## Check for (erased) subtypes
To check whether a type is an erased subtype of another we walk along the
inheritance hierarchy of the (presumably) subtype and check whether we find a
type with the same qualified name as the (erased) supertype. If this is the
case we stop the search and return success.

### Currently not implemented features
- This does not take reference subtyping rules into account: `List<?>` is not a
  subtype of `List<Object>` but the current implementation will tell you it is
  as it only compares the `List` type.

## Adapt single types
Adapting a type is the process of converting a generic type or type variable
from the class it is declared in to a subclass. This can change the type quite
dramatically as the following examples show:
```java
interface Parent<T> {}
interface Middle<Q> extends Parent<Q> {}
interface Bottom extends Middle<String> {}
```
Adapting `T` to `Middle` returns `Q`, as the type parameter was renamed in the
subclass. Adapting `Q` (or `T`) to `Bottom` returns `String` as the subclass
substitutes a concrete type.

Adaption currently works like this:
1. We compute the *hierarchy* from our context (subclass) to the supertype.
   This hierarchy is a tree built from the subclass by examining all supertypes
   and consists of two types of nodes: Declaration and Glue nodes.

   *Declaration nodes* are created for each *type* we visit along the way and
   store their formal type parameters in the order they appear in the type.

   *Glue nodes* are created for each *type use*. If we have a class `interface
   Foo<T> extends Bar<T>`, then `Bar<T>` is a *use* of the type `Bar` that
   glues the two declaration nodes (for `Foo` and `Bar`) together. Glue nodes
   can translate type parameters (e.g. convert `T` to `Q`) in the example in
   the beginning or translate a type parameter to a concrete type (e.g. `Q` to
   `String`).

   This hierarchy is then inverted so the root is the *top most* type. This is
   needed as we translate a reference from the supertype to the subtype by
   walking down the tree.

   The hierarchy for the example above would be:
   ```
   | DeclarationNode `Parent`
   | Formal Arguments: [T]
   | Children: [
   |  | GlueNode `Parent`
   |  | Actual Arguments: [Q]
   |  | Children: [
   |  |  | DeclarationNode `Middle`
   |  |  | Formal Arguments: [Q]
   |  |  | Children: [
   |  |  |   | GlueNode `Middle`
   |  |  |   | Actual Arguments: [String]
   |  |  |   | Children: [
   |  |  |   |   | DeclarationNode `Bottom`
   |  |  |   |   | Formal Arguments: []
   |  |  |   |   | Children: []
   |  |  |   | ]
   |  |  | ]
   |  | ]
   | ]
   ```

2. If the input is a type parameter we will traverse down the tree until we
   either arrive at a leaf or find a concrete type (like `String` in the
   example above).

   If the input reference is a generic type like `List<? extends T>` it is
   split apart and each part is recursively translated.

### Type translation special cases

#### Translating formal type parameters between methods
For translating formal type parameters from one method to another we can not
apply the same algorithm, as the subclass method can rename them arbitrarily,
but we need to return the same names! Consider this:
```java
interface Parent {
  <T> void foo(T t);
}
interface Child extends Parent {
  <R> void foo(R t);
}
```
Translating `T` from `Parent#foo` to `Child#foo` should obviously return `R` -
but there is no mapping that tells us this! Thankfully method parameters are
easier to translate. We know that a program that typechecks will have a
compatible translated type *in the same place* in the subclass method as it had
in the superclass method. For this reason we can just search for a usage of `T`
in `Parent#foo` and then return the corresponding type from `Child#foo`. In the
example we would find out that `T` is used as the type for the first parameter
and would return the type of the first parameter of `Child#foo` - `R`. Which is
correct!

For this reason the type adaption method check whether it is translating a
formal type parameter declared on a method to another method - and
short-circuit the operation.

#### The diamond problem
Java is subject to a form of the diamond problem as interfaces allow for
multiple inheritance. Due to this there might actually exist *multiple paths*
down from a given root to a leaf. Thankfully Java disallows a type to implement
any type more than once with differing parameters, so we can just choose an
arbitrary path to follow. This implementation always follows the first child.

It is important to note that this will *not* cause us to get stuck by taking a
wrong turn and no longer having a path down to our target class. We build the
hierarchy from the bottom up and therefore ensure that we can get down to where
we need to go from any other node in the tree.

#### Finding no hierarchy
If we can not find any hierarchy we currently return the input type unchanged.

#### Adapting generics of enclosing classes
Java allows classes to use generics of their enclosing class, e.g.
```java
public class Outer<A> {
  public class Inner<B> {
    public void bar(A a, B b) {}
  }
}
```
If you now additionally introduce a dual inheritance relationship
```java
public class OuterSub<A1> extends Outer<A1> {
  public class InnerSub<B1> extends Outer.Inner<B1> {
    public void bar(A1 a, B1 b) {}
  }
}
```
Adapting the method from `Outer.Inner#bar` to `OuterSub.InnerSub#bar` involves
building the hierarchy from `InnerSub` to `Outer.Inner` and then adapting `A1`
and `B1`.
While this hierarchy can be used to resolve `B` to `B1`, it will not be helpful
for adapting `A` to `A1`: This generic type is passed down through a completely
different inheritance chain.

To solve this, we check whether a type parameter is declared by the class
containing the type reference.
For example, when translating `A1` we notice that even though the usage is in
`InnerSub#bar`, the declaration of the type parameter was in `OuterSub`.
Therefore, we adjust the end of our hierarchy to `Outer` instead of `Inner` and
the start to the innermost enclosing class inheriting from that, which is
`OuterSub` in our example.  
Notice that there could be *multiple* matching enclosing classes, but we
have no way to decide which one to use, and just arbitrarily resolve the
ambiguity by picking the first one.

After this adjustment of the start and end types, the rest of the translation
continues normally.

## Adapting a method to a subclass
Closely related to type adaption (but not exactly the same!) is translating
*entire methods*. As a first approach we might try to just re-use our type
adaption code and adapt the return, parameter and thrown types down to the
target class. This, however, is not sufficient *as methods might declare their
own type parameters*. Consider this example:
```java
interface Parent<T> {
  <R extends T> R foo();
}
interface Child<Q> extends Parent<Q> {}
```
If we translated all types using the type adaption algorithm above we might get
```java
R foo();
```
as there is no equivalent to `R` we can adapt to - there just is *nothing* here
which would correspond to the method type parameter with an updated bound.

What we do instead is cloning the method and copying the formal type parameters
but *adapting their bound*. The example above is first turned into:
```java
<R extends Q> R foo();
```
After this step the other types are translated using the normal algorithm. As
we keep the names of the formal type parameters the same we do not need to
rewrite the method body, but we might incur clashes with existing type variables
in the subclass (have a look at what happens when you rename `Q` to `T` in the
subclass and add a `T t` parameter to the superclass method).


## Checking for conflicting methods
It is sometimes necessary to find out whether two methods *conflict*, i.e.
whether you can not declare *both* in the same type. A simple example would be
```java
String foo();
String foo();
```
Obviously you can only have one of those in a type at a given time!
A simple way to detect this is just checking whether the names and parameter
types of the methods match.

Sadly, generic erasure makes this problem a lot more difficult. The following
two methods also conflict:
```java
void foo(List<String> list);
void foo(List<Integer> list);
```
One solution to fix this is only looking at the *type erasure* of the two
methods. And this indeed solves the issue here as the methods would erase to:
```java
void foo(List list);
void foo(List list);
```
Those methods have the same name and parameters and therefore obviously conflict.

Alas, this is still not enough. Consider this:
```java
interface Foo<T> {
  void foo(T t);
}
interface SubFoo extends Foo<String> {
  void foo(String t);
}
```
Those two methods would erase - respectively - to:
```java
void foo(Object t);
void foo(String t);
```
That's a bummer! The erasure of the two methods differs but the two methods
still very much conflict. In fact, one of them is overriding the other! In case
you are curious how this works in Java: The compiler generates a synthetic
bridge method in `SubFoo` that delegates to the more specific one:
```java
void foo(Object t) {
  this.foo((String) t);
}
```

We solve this problem by falling down into the slow path, explicitly checking
whether one method overrides the other if the parameter type erasure doesn't
match. Only doing this when needed speeds up the process.


## Checking for overrides
To detect whether one method overrides we perform a suite of basic checks:
- Do the methods have the same name?
- The same parameter count? The same parameter types?
- Is the declaring type of the first a subtype of the other?
- Are the methods static? Then they can not override anything!

If all of these check out, we grab the biggest hammer we can find in our
toolbox: Method adaption. If a method overrides another, their types' erasure
must match after adapting the superclass method to the subclass. In our conflict check example above:
```java
interface Foo<T> {
  void foo(T t);
}
interface SubFoo extends Foo<String> {
  void foo(String t);
}
```
We would adapt the superclass method to:
```java
void foo(String t);
```
Which is a match even before computing the type erasure!


## Checking whether the same method signatures are the same
If overriding was too easy, this introduces another layer of complexity. Two
methods have the same signature if they have the same types after adapting the
types.

If two methods conflict or override each other they obviously have the same
signature. But there is another way that can happen and for this *context is
everything*. In the explanation at the start we casually stated that the types
should be "adapted". But to *what*? Type adaption needs a context! The answer
isn't clear-cut here and the only user of this function
(`AllMethodsSameSignatureFunction`) does the right thing. But let's have a look
at an example to illustrate this point:
```java
interface InterfaceB<T> {
  void foo(T t);
}
class TypeA {
  void foo(Exception e) {}
}

class TypeB extends TypeA implements InterfaceB<Exception> {}
```
The interesting question here is whether `TypeA#foo` and `InterfaceB#foo` have
the same signature. If `TypeB` would not exist the answer is a resounding `No!`
- the two methods have different parameter types. `TypeB` makes this harder
though. It implements the interface with the same type `TypeA` uses in its
method and, in fact, the `foo` method `TypeB` inherits from `TypeA` is
actually the implementation `TypeB` uses!
In some sense, `TypeA#foo` "implements" `InterfaceB#foo` here and they *do*
share the same signature!

To solve this problem, `isSameSignature` first adapts *both* input methods to
its context and then passes the resulting methods to `isConflicting`. In the
example above the adaption would produce
```java
void foo(Exception t); // from InterfaceB
void foo(Exception e); // from TypeA
```
and `isConflicting` would return true. Crisis averted.


# Parting words
With this you are hopefully well-equipped to tackle the implementation. The
first drafts actually weren't much longer than this document - but over time
more and more of the problems above arose. Today, the adaption code is a bit
more lengthy but hopefully split up well enough that you can go bug hunting
after reading this document. Happy coding :)
