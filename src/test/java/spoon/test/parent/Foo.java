package spoon.test.parent;

class Foo {
	void foo() {
		int x = 3;
		x = x+1;
	}

	int bar;
	Foo foo;

	int nullParent() {
		foo.bar = 0;
		assert true : "message";
		for (int i = 0; i < 10; i++) {}
		if (true) {} else {}
		foo.foo();
		class Bar {
			int bar = 0;
		}
		return 0;
	}
}
