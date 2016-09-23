package spoon.test.invocations;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.test.invocations.testclasses.Bar;
import spoon.test.invocations.testclasses.Foo;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;

public class InvocationTest {
	@Test
	public void testTypeOfStaticInvocation() throws Exception {
		SpoonAPI launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/invocations/testclasses/", "-o", "./target/spooned/"
		});
		Factory factory = launcher.getFactory();

		CtClass<?> aClass = factory.Class().get(Foo.class);

		final List<CtInvocation<?>> elements = aClass.getElements(new AbstractFilter<CtInvocation<?>>(CtInvocation.class) {
			@Override
			public boolean matches(CtInvocation<?> element) {
				return element.getTarget() != null;
			}
		});

		assertEquals(2, elements.size());
		assertTrue(elements.get(0).getTarget() instanceof CtTypeAccess);
		assertTrue(elements.get(1).getTarget() instanceof CtTypeAccess);
	}

	@Test
	public void testTargetNullForStaticMethod() throws Exception {
		final Factory factory = build(Bar.class);
		final CtClass<Bar> barClass = factory.Class().get(Bar.class);
		final CtMethod<?> staticMethod = barClass.getMethodsByName("staticMethod").get(0);
		final CtExecutableReference<?> reference = factory.Method().createReference(staticMethod);

		try {
			final CtInvocation<?> invocation = factory.Code().createInvocation(null, reference);
			assertNull(invocation.getTarget());
		} catch (NullPointerException e) {
			fail();
		}
	}
}
