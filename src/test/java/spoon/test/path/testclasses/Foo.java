package spoon.test.path.testclasses;

import java.util.ArrayList;

class Foo<T> extends ArrayList<T> {
	Foo() {
		super();
	}
	Foo(String s) {
		this();
	}

	String toto = "salut";
	
	void foo() {
		int x = 3;
		x = x + 1;

		if (x > 2) {
			int y = 3;
		} else {
			int y = 2;
		}
	}

	String foo;
	
	class foo {}

	@java.lang.SuppressWarnings("unchecked")
	void bar(int i, int j) {
		int x = 3;
		x = x + 1;
		if (i > 5) {
			x += 1;
		}
	}
	void bar(int i) {
	}
	
	void processors(String p, String p2) {
	}
	void processors(String... p) {
	}
	
	void methodWithArgs(String[] arr) {
		methodWithArgs(null);
		processors(null, null);
	}
	
}