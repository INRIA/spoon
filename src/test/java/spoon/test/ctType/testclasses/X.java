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

class O<A extends X> {
	<B extends A> B foo() {
		return null;
	}
}

