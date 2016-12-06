package spoon.test.variable.testclasses;


import static spoon.Launcher.SPOONED_CLASSES;

public class Burritos {
	static void toto() {}

	void foo() {
		Object spoon = null;
		Object x = SPOONED_CLASSES; // cannot be written spoon.o, has to be with implicit visibility or static import
		new Thread(new Runnable() {
			@Override
			public void run() {
				Burritos.toto();
			}
		});
	}
}
