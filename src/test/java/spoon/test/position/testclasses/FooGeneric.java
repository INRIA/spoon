package spoon.test.position.testclasses;

public class FooGeneric<T extends Object> {

	public final T variable = null;

	public @Deprecated static <S> S m(int parm1) {
		return null;
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

	public FooGeneric(int arg1) {

	}
}