package spoon.test.variable.testclasses;


import com.sun.org.apache.xpath.internal.operations.Mult;

import java.util.List;

import static spoon.Launcher.SPOONED_CLASSES;

public class MultiBurritos {
	static Object spoon = "bla";
	List<String> Launcher;

	static void toto() {}

	void foo() {
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
			}
		});
	}
}
