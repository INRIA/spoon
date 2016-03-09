---
title: Comments
keywords: comments
last_updated: Mars 22, 2016
---

### Comments in Spoon

In Spoon there are different kinds of comments:

* Line comments (from // to end line)
* Block comments (from /* to */)
* Javadoc comments (from /** to */)

#### Javadoc Comments

The Javadoc comments are available via the API ```CtElement.getDocComment()``` and return a ```String```.

#### Other Comments

The block and line comments are represented with a ```CtComment``` class ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/declaration/CtComment.html)). 
This class exposes API to get the position and the content of the comment.

We also try to understand to which element they are attached.
We use simple heuristics that work well in nominal cases but it is not possible to address all specific cases.
You can receive the comments of each ```CtElement``` via the API ```CtElement.getComments()``` that returns a ```List<CtComment>```.

The reprint of the comments can be disable in the Environment.  

##### Comment Attribution

* Each comment can have multiple comments
* Comments in the same line of a statement is attached to the statement
* Comments which are alone in one line (or more than one lines) are associated to the first element following them. 
* Comments cannot be associated to other comment
* Comments at the end of a block are considered as orphans comment
* Comments in a class level is attached to the class

##### Comment Example
Class comment
```Java
// class comment
class A {
  // class comment
}
```

Statement comment
```Java
// Statement comment
int a; // Statement comment
```

Orphan comment
```Java
try {
 
} exception (Exception e) {
  // Orphan comment
}
```

Multiple line comment
```Java
// Statement comment 1
// Statement comment 2 
// Statement comment 3
int a;
```