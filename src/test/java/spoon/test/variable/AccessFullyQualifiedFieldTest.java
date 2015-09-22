package spoon.test.variable;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;
import spoon.test.TestUtils;
import spoon.test.main.MainTest;
import spoon.test.variable.testclasses.Tacos;

public class AccessFullyQualifiedFieldTest {
	@Test
	public void testCheckAssignmentContracts() throws Exception {
		final Factory factory = TestUtils.build(Tacos.class);

		MainTest.checkAssignmentContracts(factory.Package().getRootPackage());
	}
}
