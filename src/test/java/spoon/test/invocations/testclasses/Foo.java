package spoon.test.invocations.testclasses;

public class Foo {
	public Foo() {
		String.valueOf(0);
		java.lang.String.valueOf(0);
		bar("test", 42, false);
	}

	public void bar(String a, int b, boolean c) {

	}
}
