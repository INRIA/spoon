---
title: Quickstart
tags: [quickstart]
keywords: testing, quickstart
last_updated: February 10, 2015
---

## Overview

Spoon module testing is a Java library that provides a fluent api for writing assertions. 
Its main goal is to propose an easy way to test Java source code transformation.

This module is directly integrated in the spoon project and can be used as soon as the
dependency is specified in your project.

## Getting started

The Assert class is the entry point for assertion methods for different data types.
Each method in this class is a static factory for the type-specific assertion objects. 
The purpose of this class is to make test code more readable.

All methods in this class are named `assertThat` and take only one argument. For example, 
if you use the method `assertThat(File)`, you will be able to use the method 
`isEqualTo(File)` to check the equality between these two files.

```java
Assert.assertThat(new File("actual.java")).isEqualTo(new File("expected.java"));
```
