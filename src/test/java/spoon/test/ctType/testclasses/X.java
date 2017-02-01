package spoon.test.ctType.testclasses;

public class X {
	public void foo() {

	}
}

class Y extends X {

}

interface Z {
	void foo();
}

abstract class W implements Z {
}

class V implements Z {
	@Override
	public void foo() {

	}
}

class A extends X {
	@Override
	public void foo() {
		super.foo();
	}
}

class B extends X {
	public void foo() {
		super.foo();
	}
}

class C extends B {
}

class D extends C {
	public void foo() {
		super.foo();
	}
}

class MultiOverrideClass {
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
