---
title: Javadoc parsing
tags: [javadoc, javadoc-parsing]
keywords: javadoc, javadoc-parsing, comments, spoon
---

The spoon-javadoc submodule provides a parser for javadoc comments, producing a
structured syntax tree representing the comment. Each tag is parsed and
tokenized according to the rules in the javadoc specification. Additionally,
references in e.g. `@link` or `@see` tags are resolved to `CtReference`s,
allowing you to easily analyze them.

A visitor infrastructure is also provided, which eases analyzing comments or
converting them into your own format.

### Installation

To use spoon-javadoc, add the following dependency to your `pom.xml`:
```xml
<dependency>
    <groupId>fr.inria.gforge.spoon</groupId>
    <artifactId>spoon-javadoc</artifactId>
    <version>$currentVersion</version>
</dependency>
```

### Basic usage

To get started you get the raw javadoc string (including `/**` and `*/`) and
pass it to a newly created `JavadocParser`.
You then call `parse` and get back a list of elements, corresponding to text,
inline tags or block tags.
Using a `JavadocVisitor` you can then visit each of them and drill down a bit.

In the following example, javadoc is parsed and then printed out again -- but
this time with some ANSI color highlighting applied. Note that references are
pretty-printed according to `CtReference#toString()`.

```java
void example() {
    String javadoc = "/**\n" +
        " * Hello world, this is a description.\n" +
        " * How are you doing? I am just fine :)\n" +
        " * This is an inline link {@link String} and one with a {@link String label}\n" +
        " * and a {@link String#CASE_INSENSITIVE_ORDER field} and {@link String#replace(char, char) with a space}.\n" +
        " * {@link java.lang.annotation.Target @Target} chained to @Target.\n" +
        " * <p>\n" +
        " * We can also write <em>very HTML</em> {@code code}.\n" +
        " * And an index: {@index \"Hello world\" With a phrase} or {@index without Without a phrase}.\n" +
        " * {@snippet lang = java id = \"example me\" foo = 'bar':\n" +
        " *         public void HelloWorld(){ //@start region = \"foo\"\n" +
        " *            System.out.println(\"Hello World!\"); // @highlight substring=\"println\"\n" +
        " *        int a = 10;  // @start foo=bar :\n" +
        " *        int a = 10;  // @end\n" +
        " *         } // @end region=foo\n" +
        " *}\n" +
        " * <h2><a id=\"resolution\"></a>{@index \"Module Resolution\"}</h2>\n" +
        " *\n" +
        " * @param args some argument\n" +
        " * @author a poor {@literal man}\n" +
        " *      hello world\n" +
        " * @see String#contains(CharSequence) with a label\n" +
        " * @see String#replace(char, char)\n" +
        " */\n";

    List<JavadocElement> elements = new JavadocParser(
            // Raw comment string including "/*" and "**/"
            // You can get this using CtComment#getRawContent from a spoon element.
            javadoc,
            // The reference element so resolving of links works correctly.
            // Javadoc comments can use "#foo" to refer to fields/methods
            // in the current class.
            new Launcher().getFactory().Type().OBJECT.getTypeDeclaration()
    ).parse();

    for (JavadocElement element : elements) {
        System.out.print(element.accept(new ExampleVisitor()));
    }
}

private static class ExampleVisitor implements JavadocVisitor<String> {

    @Override
    public String defaultValue() {
        throw new RuntimeException("Visit method not implemented");
    }

    @Override
    public String visitInlineTag(JavadocInlineTag tag) {
        String result = "{@\033[36m" + tag.getTagType().getName() + "\033[0m";
        for (JavadocElement element : tag.getElements()) {
            result += " " + element.accept(this);
        }
        result += "}";
        return result;
    }

    @Override
    public String visitBlockTag(JavadocBlockTag tag) {
        String result = "@\033[36m" + tag.getTagType().getName() + "\033[0m ";
        for (JavadocElement element : tag.getElements()) {
            result += element.accept(this);
        }
        result += "\n";
        return result;
    }

    @Override
    public String visitText(JavadocText text) {
        return text.getText();
    }

    @Override
    public String visitReference(JavadocReference reference) {
        return "\033[31m" + reference.getReference() + "\033[0m";
    }

    @Override
    public String visitSnippet(JavadocSnippetTag snippet) {
        String result = "{@\033[36m" + snippet.getTagType().getName() + "\033[0m ";
        result += snippet.getAttributes()
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> entry.getKey() + "='" + entry.getValue() + "'")
            .collect(Collectors.joining(" "));

        result += " : ";
        for (JavadocElement element : snippet.getElements()) {
            result += element.accept(this);
        }

        result += "}\n";
        return result;
    }
}
```

<br>
This will print a version with a bit more colours:
![ANSI colored javadoc]({{ "/images/spoon_javadoc_ansi_print.png" | prepend: site.baseurl }})

### Snippets
Spoon-javadoc provides the `JavadocSnippetBody` class to help parse javadoc
snippets:
```java
JavadocSnippetBody body = JavadocSnippetBody.fromString(
    "class Foo { // @start region=\"foo\"\n" +
    "  int p0 = 0; // @start region=\"bar\"\n" +
    "  int p1 = 1;\n" +
    "  int p2 = 2; // @end\n" +
    "  int p3 = 3; // @end\n" +
    "}\n"
);
body.getLines(); // returns all lines of the original snippet
body.getRegions(); // returns all start/highlight/link regions
body.getActiveRegionsAtLine(0); // returns all regions active in the given line
```
