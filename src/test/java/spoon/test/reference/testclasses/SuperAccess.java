package spoon.test.reference.testclasses;

public class SuperAccess  extends Parent {
	@Override
	void method() {
		super.method();
	}
}

class Parent {
	void method() {}
}
