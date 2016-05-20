package spoon.test.condition.testclasses;

public class Foo {
	public boolean m() {
		boolean x;
		int a = 0;
		return x = (a == 18) ? true : false;
	}

	public boolean m2() {
		int a = 0;
		return a == 18 ? true : false;
	}
}
