---
title: Java Source Code Pretty-Printing
---

Spoon provides users with different options to pretty-print the Java code.


## Fully-qualified Pretty-Printing

Spoon can pretty-print code where all classes and methods are fully-qualified. This is the default behavior on `toString()` on AST elements.
This is not readable for humans but is useful when name collisions happen. If `launcher.getEnvironment().getToStringMode() == FULLYQUALIFIED`, the files written on disk are also fully qualified. 


## Autoimport Pretty-Printing

Spoon can pretty-print code where all classes and methods are imported as long as no conflict exists.

```java
launcher.getEnvironment().setAutoImports(true);
```

The autoimport mode computes the required imports, add the imports in the pretty-printed files, and writes class names unqualified (w/o package names). This involves changing the field `implicit` of some elements of the model, through a set of `ImportAnalyzer`, most notable `ImportCleaner` and `ImportConflictDetector`.
When pretty-printing, Spoon reformats the code according to its own formatting rules that can be configured by providing a custom `TokenWriter`.

## Sniper Pretty-Printing Mode

 The sniper mode enables to rewrite only the transformed AST elements, so that the rest of the code is printed identically to the origin version. This is useful to get small diffs after automated refactoring. 

```java
launcher.getEnvironment().setPrettyPrinterCreator(() -> {
   return new SniperJavaPrettyPrinter(launcher.getEnvironment());
  }
);
```

## User-Defined Pretty-Printing Mode

It is a good practice to have consistent formatting in a project.
Spoon can be used to implement a custom Java pretty-printer, according to the guidelines of your project.
For instance, this can be used to write a pretty-printer according to a custom Checkstyle ruleset.

The idea is to configure `DefaultJavaPrettyPrinter` with an appropriate `TokenWriter`. Subclasses of [`TokenWriter`](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/TokenWriter.html) can override a number of pretty-printing methods.

For instance, here is an example of a pretty-printer that writes two spaces before each keyword (see source in [`testCustomPrettyPrinter`](https://github.com/INRIA/spoon/blob/master/src/test/java/spoon/test/prettyprinter/PrinterTest.java)):

```java
Launcher spoon = new Launcher();
// Java file to be pretty-printed, can be a folder as well
spoon.addInputResource("src/test/resources/JavaCode.java");
spoon.getFactory().getEnvironment().setPrettyPrinterCreator(() -> {
    DefaultJavaPrettyPrinter defaultJavaPrettyPrinter = new DefaultJavaPrettyPrinter(spoon.getFactory().getEnvironment());
    // here we create the custom version of the token writer
    defaultJavaPrettyPrinter.setPrinterTokenWriter(new DefaultTokenWriter() {
      @Override
      public DefaultTokenWriter writeKeyword(String token) {
        // write two spaces and then the keyword
        getPrinterHelper().write("   " + token);
        return this;
      }
    });
    return defaultJavaPrettyPrinter;
});
spoon.run();
```

That's it! The pretty-printed code is in folder ./spooned (default value that can be set with setOutputDirectory).
