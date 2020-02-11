---
title: Command Line
tags: [usage]
keywords: command, line, usage, java, jar
---

To run Spoon in command line, you first have to download the corresponding jar file on [Maven Central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22fr.inria.gforge.spoon%22%20AND%20a%3A%22spoon-core%22) 
(take the version with all dependencies).

When you have downloaded the desired version of Spoon, you can directly use it. 
To know how you use the jar file, launch it with `--help` argument. 
You see the output at the end of this page for the current release version of Spoon.

The basic usage of Spoon consists in defining the original source location and the list of compiled processors to be used. 

```console
$ java -classpath /path/to/binary/of/your/processor.jar:spoon-core-{{site.spoon_release}}-with-dependencies.jar spoon.Launcher -i /path/to/src/of/your/project -p fr.inria.gforge.spoon.processors.CatchProcessor
```

If you plan to repeatedly run Spoon from the command line, it may be a good idea to combine all of your commands into a single bash script. An example of this can be found [here](https://github.com/INRIA/spoon/blob/master/doc/example_scripts/example_usage_script.sh).

Note that when you use Spoon in command line, you manually handle the classpath.
In particular, if the to-be-transformed source files depend on libraries, specify them with the `--source-classpath` flag.

```console

Options : 

  [-h|--help]

  [--tabs]
        Use tabulations instead of spaces in the generated code (use spaces by
        default).

  [--tabsize <tabsize>]
        Define tabulation size. (default: 4)

  [--level <level>]
        Level of the ouput messages about what spoon is doing. (default: OFF)

  [--with-imports]
        Enable imports in generated files.

  [--compliance <compliance>]
        Java source code compliance level (1,2,3,4,5, 6, 7 or 8). (default: 8)

  [--encoding <encoding>]
        Forces the compiler to use a specific encoding (UTF-8, UTF-16, ...).
        (default: UTF-8)

  [(-i|--input) <input>]
        List of path to sources files.

  [(-p|--processors) <processors>]
        List of processor's qualified name to be used.

  [(-t|--template) <template>]
        List of path to templates java files.

  [(-o|--output) <output>]
        Specify where to place generated java files. (default: spooned)

  [--source-classpath <source-classpath>]
        An optional classpath to be passed to the internal Java compiler when
        building or compiling the input sources.

  [--template-classpath <template-classpath>]
        An optional classpath to be passed to the internal Java compiler when
        building the template sources.

  [(-d|--destination) <destination>]
        An optional destination directory for the generated class files.
        (default: spooned-classes)

 --cpmode <cpmode>
        Classpath mode to use in Spoon: NOCLASSPATH; FULLCLASSPATH (default:
        NOCLASSPATH)

  [--output-type <output-type>]
        States how to print the processed source code:
        nooutput|classes|compilationunits (default: classes)

  [--compile]
        Enable compilation and output class files.

  [--lines]
        Set Spoon to try to preserve the original line numbers when generating
        the source code (may lead to human-unfriendly formatting).

  [-g|--gui]
        Show spoon model after processing

  [-r|--no-copy-resources]
        Disable the copy of resources from source to destination folder.

  [-j|--generate-javadoc]
        Enable the generation of the javadoc. Deprecated, use enable-comments
        argument.

  [-c|--enable-comments]
        Adds all code comments in the Spoon AST (Javadoc, line-based comments),
        rewrites them when pretty-printing.

  [(-f|--generate-files) <generate-files>]
        Only generate the given fully qualified java classes (separated by ':'
        if multiple are given).

  [-a|--disable-model-self-checks]
        Disables checks made on the AST (hashcode violation, method's signature
        violation and parent violation). Default: false.
```
