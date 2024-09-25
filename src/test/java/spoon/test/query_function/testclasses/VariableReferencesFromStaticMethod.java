package spoon.test.query_function.testclasses;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VariableReferencesFromStaticMethod {
	int field = 1;
	
	static void staticMethod() {
		int field = 2;
		assertTrue(field == 2);
	}
}
