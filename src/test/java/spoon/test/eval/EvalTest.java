package spoon.test.eval;

import static org.junit.Assert.assertEquals;
import static spoon.test.TestUtils.build;

import org.junit.Test;

import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;

public class EvalTest {

	@Test
	public void testStringConcatenation() throws Exception {
		CtClass<?> type = build("spoon.test.eval", "ToEvaluate");
		assertEquals("ToEvaluate", type.getSimpleName());
		
		CtBlock<?> b = type.getMethodsByName("testStrings").get(0).getBody();
		assertEquals(4, b.getStatements().size());
		b = b.partiallyEvaluate();
		b = type.getMethodsByName("testInts").get(0).getBody();
		assertEquals(1, b.getStatements().size());
		b = b.partiallyEvaluate();
		assertEquals(0, b.getStatements().size());		
	}


}
