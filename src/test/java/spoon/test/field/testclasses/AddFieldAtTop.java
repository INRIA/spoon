package spoon.test.field.testclasses;

public class AddFieldAtTop {

	static {
	}

	int i;

	void m() {
	}

	class Foo {
		int i;
		void m() {
			int x = i;
		}
	}
}
