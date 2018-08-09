package spoon.test.position.testclasses;

public class FooMethod {

	public static void m(int parm1) {
		return;
	}

	/**
	 * Method with javadoc
	 * @param parm1 the parameter
	 */
	int mWithDoc(int parm1) {
		return parm1;
	}


	public
	static
	final
	int mWithLine
			(int parm1) {
		return parm1;
	}

	public FooMethod(int arg1) {

	}
	public void emptyMethod() {}
}