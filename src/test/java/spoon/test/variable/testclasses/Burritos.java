package spoon.test.variable.testclasses;


import static spoon.Launcher.SPOONED_CLASSES;

public class Burritos {
	void foo() {
		Object spoon = null;
		Object xx = SPOONED_CLASSES; // cannot be written spoon.o, has to be with implicit visibility or static import
	}
}
