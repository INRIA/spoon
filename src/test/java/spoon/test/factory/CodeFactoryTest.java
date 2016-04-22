package spoon.test.factory;

import org.junit.Test;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.createFactory;

public class CodeFactoryTest {
	@Test
	public void testThisAccess() throws Exception {
		final Factory factory = createFactory();
		final CtTypeReference<Object> type = factory.Type().createReference("fr.inria.Test");
		final CtThisAccess<Object> thisAccess = factory.Code().createThisAccess(type);

		assertNotNull(thisAccess.getTarget());
		assertTrue(thisAccess.getTarget() instanceof CtTypeAccess);
		assertEquals(type, ((CtTypeAccess) thisAccess.getTarget()).getAccessedType());
	}
}
