package spoon.test.ctType.testclasses;

class X {
	public void foo() {

	}
}

class Y extends X {

}

interface Z {
	default void foo() {

	}
}

class W implements Z {

}

class A implements Z {
	@Override
	public void foo() {

	}
}
