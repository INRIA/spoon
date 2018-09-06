package spoon.test.position.testclasses;

public class MethodWithJavaDocAndModifiers {

	/**
	 * Method with javadoc
	 * @param parm1 the parameter
	 */
	public @Deprecated int mWithDoc(int parm1) {
		return parm1;
	}
}