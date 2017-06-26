package spoon.test.variable.testclasses;

public class BurritosStaticMethod {
	static void toto() {}

	void foo() {
		Object spoon = null;

		new Thread(new Runnable() {
			@Override
			public void run() {
				toto();
			}
		});
	}
}
