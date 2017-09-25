package spoon.test.query_function.testclasses;

import static org.junit.Assert.assertTrue;

public class VariableReferencesFromStaticMethod {
	int field = 1;
	
	static void staticMethod() {
		int field = 2;
		assertTrue(field == 2);
	}
}
