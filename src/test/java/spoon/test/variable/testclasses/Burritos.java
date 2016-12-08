package spoon.test.variable.testclasses;

import java.util.Map;

import static spoon.Launcher.SPOONED_CLASSES;
import static spoon.test.variable.testclasses.ForStaticVariables.Map;

public class Burritos {

	Map uneMap;
	String bla = Map;

	void foo() {
		Object spoon = null;
		Object x = SPOONED_CLASSES; // cannot be written spoon.o, has to be with implicit visibility or static import
	}
}
