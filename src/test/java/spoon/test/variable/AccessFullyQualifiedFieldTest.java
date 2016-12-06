package spoon.test.variable;

import org.junit.Test;
import spoon.reflect.factory.Factory;
import spoon.test.main.MainTest;
import spoon.test.variable.testclasses.Tacos;

import static spoon.testing.utils.ModelUtils.build;

public class AccessFullyQualifiedFieldTest {
	@Test
	public void testCheckAssignmentContracts() throws Exception {
		final Factory factory = build(Tacos.class);

		MainTest.checkAssignmentContracts(factory.Package().getRootPackage());
	}
}
