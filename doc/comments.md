---
title: Comments
keywords: comments
last_updated: May 25, 2016
---

In Spoon there are four different kinds of comments:

* File comments (comment at the begin of the file, generally licence) `CtComment.CommentType.FILE`
* Line comments (from // to end line) `CtComment.CommentType.INLINE`
* Block comments (from /* to */) `CtComment.CommentType.BLOCK`
* Javadoc comments (from /** to */) `CtComment.CommentType.JAVADOC`

The comments are represented in Spoon with a `CtComment` class ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtComment.html)). 
This class exposes API to get the content of the comment `CtComment.getContent()`, the type of the comment `CtComment.getCommentType()` and the position `CtComment.getPosition()`.

We also try to understand to which element they are attached.
We use simple heuristics that work well in nominal cases but it is not possible to address all specific cases.
You can receive the comments of each `CtElement` via the API `CtElement.getComments()` which returns a `List<CtComment>`.

The parsing of the comments can be disable in the Environment via the option `Environment.setCommentsEnable(boolean)` or the argument `--enable-comments` (or `-c`) with the command line.  

## Javadoc Comments

The Javadoc comments are also available via the API `CtElement.getDocComment()` but this API returns directly the content of the Javadoc as `String`.

## Comment Attribution

* Each comment can have multiple comments
* Comments in the same line of a statement is attached to the statement
* Comments which are alone in one line (or more than one lines) are associated to the first element following them. 
* Comments cannot be associated to other comment
* Comments at the end of a block are considered as orphans comment
* Comments in a class level is attached to the class

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
