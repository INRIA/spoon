package spoon.test.targeted;

import org.junit.Test;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.test.targeted.testclasses.InternalSuperCall;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static spoon.test.TestUtils.build;

public class TargetedExpressionTest {
	@Test
	public void testTargetOfSuperAccesses() throws Exception {
		final Factory factory = build(InternalSuperCall.class);
		final CtClass<?> ctClass = factory.Class().get(InternalSuperCall.class);
		final List<CtSuperAccess> superAccesses = ctClass.getElements(new AbstractFilter<CtSuperAccess>(CtSuperAccess.class) {
			@Override
			public boolean matches(CtSuperAccess element) {
				return super.matches(element);
			}
		});
		assertEquals(2, superAccesses.size());
		assertNull(superAccesses.get(0).getTarget());
		assertNotNull(superAccesses.get(1).getTarget());

		CtMethod<?> method = ctClass.getElements(new NameFilter<CtMethod<?>>("methode")).get(0);
		assertEquals(
				"spoon.test.targeted.testclasses.InternalSuperCall.super.toString()",
				method.getBody().getStatements().get(0).toString());

		CtMethod<?> toString = ctClass.getElements(new NameFilter<CtMethod<?>>("toString")).get(0);
		assertEquals(
				"return super.toString()",
				toString.getBody().getStatements().get(0).toString());
	}
}
