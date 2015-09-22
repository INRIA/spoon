---
title: Command Line
tags: [usage]
keywords: command, line, usage, java, jar
last_updated: October 1, 2015
---

To run Spoon in command line, you first have to download the corresponding jar file.
There are two ways to download it:

- You can download it from [INRIA's gforge](https://gforge.inria.fr/frs/?group_id=73) 
(take the version with all dependencies).
- You can download it from [Maven Central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22fr.inria.gforge.spoon%22%20AND%20a%3A%22spoon-core%22) 
(take the version with all dependencies).

When you have downloaded the desired version of Spoon, you can directly use it. 
To know how you use the jar file, launch it with `--help` argument. 
You see the output at the end of this page for the current release version of Spoon.

The basic usage of Spoon consists in defining the original source location and the list of compiled processors to be used. 

```console
$ java -classpath /path/to/binary/of/your/processor.jar:spoon-core-4.2.0-jar-with-dependencies.jar -jar spoon-core-4.2.0-jar-with-dependencies.jar -i /path/to/src/of/your/project -p fr.inria.gforge.spoon.processors.CatchProcessor
```

Note that when you use Spoon in command line, you manually handle the classpath.
In particular, if the to-be-transformed source files depend on libraries, specify them with the `--source-classpath` flag.

```console
$ java -jar spoon-core-{{site.spoon_release}}-jar-with-dependencies.jar --help
Spoon version {{site.spoon_release}} 
Usage: java <launcher name> [option(s)]

Options : 

  [-h|--help]

  [-v|--verbose]
        Argument deprecated, see the argument level. Output messages about what
        the compiler is doing.

  [--tabs]
        Use tabulations instead of spaces in the generated code (use spaces by
        default).

  [--tabsize <tabsize>]
        Define tabulation size. (default: 4)

  [--level <level>]
        Level of the ouput messages about what spoon is doing. Default value is
        ALL level. (default: OFF)

  [--vvv]
        Argument deprecated, see the argument level. Generate all debugging
        info.

  [--with-imports]
        Enable imports in generated files.

  [--compliance <compliance>]
        Java source code compliance level (1,2,3,4,5, 6, 7 or 8). (default: 8)

  [--encoding <encoding>]
        Forces the compiler to use a specific encoding (UTF-8, UTF-16, ...).
        (default: UTF-8)

  [(-s|--spoonlet) <spoonlet>]
        List of spoonlet files to load.

  [(-i|--input) <input>]
        List of path to sources files.

  [(-p|--processors) <processors>]
        List of processor's qualified name to be used.

  [(-t|--template) <template>]
        List of path to templates java files.

  [(-o|--output) <output>]
        Specify where to place generated java files. (default: spooned)

  [--properties <properties>]
        Directory to search for spoon properties files.

  [--source-classpath <source-classpath>]
        An optional classpath to be passed to the internal Java compiler when
        building or compiling the input sources.

  [--template-classpath <template-classpath>]
        An optional classpath to be passed to the internal Java compiler when
        building the template sources.

  [(-d|--destination) <destination>]
        An optional destination directory for the generated class files.
        (default: spooned-classes)

  [--output-type <output-type>]
        States how to print the processed source code:
        nooutput|classes|compilationunits (default: classes)

  [--compile]
        Enable compilation and output class files.

  [--precompile]
        Enable pre-compilation of input source files before processing. Compiled
        classes will be added to the classpath so that they are accessible to
        the processing manager (typically, processors, annotations, and
        templates should be pre-compiled most of the time).

  [--buildOnlyOutdatedFiles]
        Set Spoon to build only the source files that have been modified since
        the latest source code generation, for performance purpose. Note that
        this option requires to have the --ouput-type option not set to none.
        This option is not appropriate to all kinds of processing. In particular
        processings that implement or rely on a global analysis should avoid
        this option because the processor will only have access to the outdated
        source code (the files modified since the latest processing).

  [--lines]
        Set Spoon to try to preserve the original line numbers when generating
        the source code (may lead to human-unfriendly formatting).

  [-x|--noclasspath]
        Does not assume a full classpath

  [-g|--gui]
        Show spoon model after processing

  [-r|--no-copy-resources]
        Disable the copy of resources from source to destination folder.

  [-j|--generate-javadoc]
        Enable the generation of the javadoc.
```