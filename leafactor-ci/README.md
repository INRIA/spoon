# java-refactoring

[![Build Status](https://travis-ci.com/TQRG/leafactor-ci.svg?token=35rpGpzubsgs2UqfNV5N&branch=master)](https://travis-ci.com/TQRG/leafactor-ci)

# Requirements

Java 8 JDK - Make sure you have the Java 8 JDK and its referenced in the JAVA_HOME environment variable.

# Tests

To run tests use:
```
gradlew :cleanTest :test --tests "com.leafactor.cli.rules.TestRules"
```

Note: Tests in IntelliJ look unorganized because gradle currently has no support for dynamic test names, [see more.](https://github.com/gradle/gradle/issues/5975) 

# Publishing
The publishing version can be found in the gradle.properties file. A version can only be published once.
To publish a version we must have a previously initialized login session to the gradle plugin registry, to sign in use:

```
gradlew login
```

After authorization is granted, we can proceed to the publishing of the version.

```
gradlew publishPlugins
``` 

Note:
If the task fails due to javadoc problems, execute the following command and try again:
```
gradlew javadoc
```
