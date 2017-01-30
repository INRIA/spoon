package spoon.test.query_function.testclasses;

import static org.junit.Assert.assertTrue;

public class ClassC {

	//package protected field
	int field = 300;
	int packageProtectedField = 301;
	private int privateField = 302;
	protected int protectedField = 303;
	public int publicField = 304;
	
	public ClassC() {
		assertTrue(field == 300);
		assertTrue(packageProtectedField == 301);
		assertTrue(privateField == 302);
		assertTrue(protectedField == 303);
		assertTrue(publicField == 304);
	}

}
