package com.example;

public class Class1 {
	Class2 doSomething() {
		return new Class2(); // should be resolved if packages are correctly merged.
	}
}