package spoon.test.generics.testclasses6;

public class A<X> {

	<T> void m1(T a) {
	}

	void f() {
		this.<Integer>m1(42);
	}
}
