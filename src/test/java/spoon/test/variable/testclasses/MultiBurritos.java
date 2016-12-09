package spoon.test.variable.testclasses;

import java.util.List;

import static spoon.Launcher.SPOONED_CLASSES;
import static spoon.test.variable.testclasses.ForStaticVariables.foo;

public class MultiBurritos {
	static Object spoon = "bla";
	List<String> Launcher;

	static void toto() {}

	void bar() {
		Object spoon = null;
		Object x = SPOONED_CLASSES; // cannot be written spoon.o, has to be with implicit visibility or static import
		Launcher.isEmpty();
	}

	void bidule() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				MultiBurritos.toto();
				MultiBurritos.spoon = "truc";
				foo();

			}
		});
	}
}
