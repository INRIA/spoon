package spoon.test.executable;

public class ExecutableRefTestSource implements MyIntf {

	public void testMethod() {
		String.valueOf("Hello World");
	}

	public void testConstructor() {
		new String("Hello World");
	}

	@Override
	public void myMethod() {
	}
}
