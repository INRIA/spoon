package spoon.test.trycatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static spoon.test.TestUtils.build;

import org.junit.Test;

import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

public class TryCatchTest {

	@Test 
	public void testModelBuildingInitializer() throws Exception {
		CtClass<Main> type = build ("spoon.test.trycatch", "Main");
		assertEquals("Main", type.getSimpleName());

		CtMethod<Void> m = type.getMethod("test");
		assertNotNull(m);
		assertEquals(2, m.getBody().getStatements().size());
		assertTrue(m.getBody().getStatements().get(0) instanceof CtTry);
		assertTrue(m.getBody().getStatements().get(1) instanceof CtTry);
		CtTry t1 = m.getBody().getStatement(0);
		assertTrue(t1.getResources().isEmpty());
		CtTry t2 = m.getBody().getStatement(1);
		assertNotNull(t2.getResources());
	}
}
