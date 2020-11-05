package spoon.architecture.defaultchecks.forbiddeninvocation;

public class NoError {

	int a = 5;

	public void bar() {
		Integer.toHexString(a);
	}
}
