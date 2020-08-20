---
title: Custom Java Pretty-Printing
---

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
