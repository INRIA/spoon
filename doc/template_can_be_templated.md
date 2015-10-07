---
title: What can be templated?
tags: [template]
keywords: template, substitution, code, java
last_updated: October 7, 2015
---

All meta-model elements can be templated. For instance, one can 
template a try/catch block as shown in the class `TryCatchOutOfBoundTemplate`. 
This template type-checks, and can be used as input by the substitution 
engine to wrap a method body into a try/catch block. The substitution engine 
contains various methods that implement different substitution scenarios. 
For instance, method `insertAllMethods` inserts all the methods of a template 
in an existing class. It can be used for instance, to inject getters and setters.


```java
public class TryCatchOutOfBoundTemplate extends BlockTemplate {
	TemplateParameter<Void> _body_; // the body to surround

	@Override
	public void block() {
		try {
			_body_.S();
		} catch (OutOfBoundException e) {
			e.printStackTrace();
		}
	}
}
```