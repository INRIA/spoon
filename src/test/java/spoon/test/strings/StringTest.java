package spoon.test.strings;

import org.junit.Test;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.TypeFilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

public class StringTest {

	@Test
	public void testModelBuildingInitializer() throws Exception {
		CtClass<?> type = build("spoon.test.strings.testclasses", "Main");
		assertEquals("Main", type.getSimpleName());

		CtBinaryOperator<?> op = type.getElements(
				new TypeFilter<CtBinaryOperator<?>>(CtBinaryOperator.class))
				.get(0);
		assertEquals("(\"a\" + \"b\")", op.toString());
		assertEquals(BinaryOperatorKind.PLUS, op.getKind());
		assertTrue(op.getLeftHandOperand() instanceof CtLiteral);
		assertTrue(op.getRightHandOperand() instanceof CtLiteral);
	}
}
