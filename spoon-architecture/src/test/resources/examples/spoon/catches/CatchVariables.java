package examples.spoon.catches;

import java.io.IOError;

public class CatchVariables {

	public void bar() {
		try {

		} catch (IllegalAccessError e) {
		}

		try {

		} catch (IOError e) {
			break;
		}
	}
}
