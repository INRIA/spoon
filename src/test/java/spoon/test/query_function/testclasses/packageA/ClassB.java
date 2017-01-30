package spoon.test.query_function.testclasses.packageA;

import static org.junit.Assert.assertTrue;

public class ClassB {

	//package protected field
	int field = 200;
	int packageProtectedField = 201;
	private int privateField = 202;
	protected int protectedField = 203;
	public int publicField = 204;
	
	public ClassB() {
		assertTrue(field == 200);
		assertTrue(packageProtectedField == 201);
		assertTrue(privateField == 202);
		assertTrue(protectedField == 203);
		assertTrue(publicField == 204);
	}

}
