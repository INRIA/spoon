package spoon.test.constructorcallnewclass.testclasses;

public class Foo {
	int i;

	public Foo() {
	}

	public Foo(int i) {
		this.i = i;
	}

	public void m() {
		new String();
		new String("");
		new Foo();
		new Foo(42);
	}

	public void m2() {
		new Object() {
		};
		new Bar() {
		};
		new Tacos<String>() {
		};
		new BarImpl(1) {
		};
	}

	public interface Bar {
	}

	public interface Tacos<K> {
	}

	public class BarImpl implements Bar {
		int i;

		public BarImpl(int i) {
			this.i = i;
		}
	}
}
