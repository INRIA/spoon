package spoon.test.visibility.testclasses;

public class A<T> {
	public class B {
	}

	public class C<T> {
	}

	public boolean m(Object o) {
		return o instanceof A.B;
	}

	public C<T> m() {
		return new C<T>();
	}

	public void aMethod() {
		class D {
		}
		new D();
	}
}
