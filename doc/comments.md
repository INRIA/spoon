---
title: Comments and position
keywords: comments position
---

# Comment

In Spoon there are four different kinds of comments:

* File comments (comment at the begin of the file, generally licence) `CtComment.CommentType.FILE`
* Line comments (from // to end line) `CtComment.CommentType.INLINE`
* Block comments (from /* to */) `CtComment.CommentType.BLOCK`
* Javadoc comments (from /** to */) `CtComment.CommentType.JAVADOC`

The comments are represented in Spoon with a `CtComment` class ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtComment.html)). 
This class exposes an API to get the content `CtComment.getContent()`, the type `CtComment.getCommentType()` and the position `CtComment.getPosition()` of an comment.

We also try to understand to which element they are attached.
We use some simple heuristics that work well in nominal cases but cannot address all specific cases.
You can retrieve the comments of each `CtElement` via the API `CtElement.getComments()` which returns a `List<CtComment>`.

The parsing of the comments can be enabled in the Environment via the option `Environment.setCommentEnabled(boolean)` or the command line argument `--enable-comments` (or `-c`).  

## Javadoc Comments

The Javadoc comments are also available via the API `CtElement.getDocComment()` but this API returns directly the content of the Javadoc as `String`.

## Comment Attribution

* Each element can have multiple comments
* Comments in the same line of a statement are attached to the statement
* Comments which are alone in one line (or more than one line) are associated to the first element following them
* Comments cannot be associated to other comments
* Comments at the end of a block are considered as orphan comments
* Comments before a class definition are attached to the class

### Comment Examples

Class comment

```java
// class comment
class A {
  // class comment
}
```

Statement comment

```java
// Statement comment
int a; // Statement comment
```

Orphan comment

```java
try {
 
} exception (Exception e) {
  // Orphan comment
}
```

Multiple line comment

```java
// Statement comment 1
// Statement comment 2 
// Statement comment 3
int a;
```

## Process Comments

You can process comments like every `CtElement`.

```java
public class CtCommentProcessor extends AbstractProcessor<CtComment> {
    
    @Override
    public boolean isToBeProcessed(CtComment candidate) {
        // only process Javadoc
        if (candidatate.getCommentType() == CtComment.CommentType.JAVADOC) {
            return true;
        }
        return false;
    }

    @Override
    public void process(CtComment ctComment) {
        // process the ctComment
    } 
}
```

# Source Position

`SourcePosition` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/cu/SourcePosition.html)) defines the position of the `CtElement` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/declaration/CtElement.html)) in the original source file. 
SourcePosition is extended by three specialized positions:

- `DeclarationSourcePosition` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/cu/position/DeclarationSourcePosition.html)) 
- `BodyHolderSourcePosition` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/cu/position/BodyHolderSourcePosition.html)).

These three specializations are used to define the position of specific CtElement.
For example DeclarationSourcePosition is used to define the position of all declarations (variable, type, method, ...).
This provides an easy access to the position of the modifiers and the name.
The BodyHolderSourcePosition is used to declare the position of all elements that have a body.
