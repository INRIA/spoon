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

class P<D extends X, F> extends O<D> {
	@Override
	<E extends D> E foo() {
		return null;
	}
}

class K<A extends List<? extends X>> {
	<B extends A> void m(List<? extends B> l) {}
}
