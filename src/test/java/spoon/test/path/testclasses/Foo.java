package spoon.test.path.testclasses;

class Foo {
	Foo() {
		super();
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

	@java.lang.SuppressWarnings("unchecked")
	void bar(int i, int j) {
		int x = 3;
		x = x + 1;
		if (i > 5) {
			x += 1;
		}
	}
}