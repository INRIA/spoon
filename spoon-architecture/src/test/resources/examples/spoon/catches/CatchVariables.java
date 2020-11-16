package examples.spoon.catches;

public class CatchVariables {

	public void bar() {
		try {

		} catch (IllegalAccessError | IllegalAccessException e) {
		}

		try {

		} catch (Throwable e) {
			break;
		}
	}
}
