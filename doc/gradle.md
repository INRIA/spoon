---
title: Gradle
tags: [usage]
keywords: gradle, usage, java, plugin
last_updated: October 1, 2015
---

## Dependency

```groovy
compile 'fr.inria.gforge.spoon:spoon-core:{{site.spoon_release}}'
```

You may also have to add our repository, see below.


## Plugin

A Gradle plugin allows easily Spoon when using Gradle.
This plugin isn't available in Maven Central yet. Clone the Git project 
on your computer and install it on your system. To do that, clone the 
official [GitHub project](https://github.com/SpoonLabs/spoon-gradle-plugin) 
of this plugin and launch the command line `./gradlew clean install` at 
the root directory of the plugin project. After that, use it in your 
`build.gradle` files of your project.


```groovy
buildscript {
    repositories {
        mavenLocal()
        maven {
          url 'http://spoon.gforge.inria.fr/repositories/'
        }
    }
    dependencies {
        classpath group: 'fr.inria.gforge.spoon', 
                  name: 'spoon-gradle-plugin', 
                  version:'1.0-SNAPSHOT'
    }
}

apply plugin: 'java'
apply plugin: 'spoon'

spoon {
    processors = ['fr.inria.gforge.spoon.processors.CatchProcessor']
}
```

You simply specify your processors, in fully qualified name, in the configuration 
of the plugin, the processors will be applied on your target project before compilation.

In short, the Gradle plugin gives the classpath of your project to Spoon, 
applies Spoon on all source directories and rewrites the transformed Java files in the build 
directory. These parameters can be changed in the configuration of the plugin. 

{{site.data.alerts.warning}}
If you want use a processor which isn't in your target project, specify the dependency 
as classpath in the buildscript of your project where the Gradle plugin can retrieve it. 
Otherwise, your processor won't be applied.
{{site.data.alerts.end}}

To know more about this Maven plugin, you can check the README of its [GitHub project](https://github.com/SpoonLabs/spoon-gradle-plugin). 
