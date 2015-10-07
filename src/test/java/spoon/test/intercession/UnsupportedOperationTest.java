package spoon.test.intercession;

import org.junit.Test;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.factory.Factory;
import spoon.test.TestUtils;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class UnsupportedOperationTest {
	@Test
	public void testDefaultExpressionOfCtCatchVariable() throws Exception {
		final Factory factory = TestUtils.createFactory();
		final CtCatchVariable<Object> catchVariable = factory.Core().createCatchVariable();

		try {
			catchVariable.setDefaultExpression(factory.Core().createCodeSnippetExpression());
			fail("setDefaultExpression must throw an UnsupportedOperationException exception!");
		} catch (UnsupportedOperationException ignore) {
		}

		assertNull(catchVariable.getDefaultExpression());
	}

	@Test
	public void testDefaultExpressionOfCtParameter() throws Exception {
		final Factory factory = TestUtils.createFactory();
		final CtParameter<Object> parameter = factory.Core().createParameter();

		try {
			parameter.setDefaultExpression(factory.Core().createCodeSnippetExpression());
			fail("setDefaultExpression must throw an UnsupportedOperationException exception!");
		} catch (UnsupportedOperationException ignore) {
		}

		assertNull(parameter.getDefaultExpression());
	}
}
