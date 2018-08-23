package spoon.test.reference.testclasses;

public class FooBar {
	public class Bar {
	}

	public class Foo {
	}

	public Foo getFoo() {
		return new Foo() {
			public void printString(String myArgFoo) {
				System.out.println(myArgFoo);
			}
		};
	}

	public Bar getBar() {
		return new Bar() {
			public void printString(String myArg) {
				System.out.println(myArg);
			}
		};
	}
}