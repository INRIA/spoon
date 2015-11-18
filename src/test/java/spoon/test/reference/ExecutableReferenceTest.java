package spoon.test.reference;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.reference.testclasses.Burritos;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class ExecutableReferenceTest {
	@Test
	public void testCallMethodOfClassNotPresent() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/resources/executable-reference", "-o", "./target/spooned/test/resources", "--noclasspath"
		});
		final List<CtExecutableReference<?>> references = Query.getReferences(launcher.getFactory(), new ReferenceTypeFilter<CtExecutableReference<?>>(CtExecutableReference.class) {
			@Override
			public boolean matches(CtExecutableReference<?> reference) {
				return !reference.isConstructor() && super.matches(reference);
			}
		});

		final List<CtInvocation<?>> invocations = Query.getElements(launcher.getFactory(), new TypeFilter<CtInvocation<?>>(CtInvocation.class) {
			@Override
			public boolean matches(CtInvocation<?> element) {
				return !element.getExecutable().isConstructor() && super.matches(element);
			}
		});

		assertEquals(4, references.size());
		assertEquals(4, invocations.size());

		// Executable reference with 0 parameter.
		final CtExecutableReference<?> executableZeroParameter = references.get(0);
		assertNotNull(executableZeroParameter.getDeclaringType());
		assertNull(executableZeroParameter.getType());
		assertEquals(0, executableZeroParameter.getParameters().size());
		assertEquals("Bar#m()", executableZeroParameter.toString());
		assertEquals("new Bar().m()", invocations.get(0).toString());

		// Executable reference with 1 parameter and return type.
		final CtExecutableReference<?> executableOneParameter = references.get(1);
		assertNotNull(executableOneParameter.getDeclaringType());
		assertNotNull(executableOneParameter.getType());
		assertEquals(1, executableOneParameter.getParameters().size());
		assertNotEquals(executableZeroParameter, executableOneParameter);
		assertEquals("Bar#m(int)", executableOneParameter.toString());
		assertEquals("bar.m(1)", invocations.get(1).toString());

		// Executable reference with 2 parameters.
		final CtExecutableReference<?> executableTwoParameters = references.get(2);
		assertNotNull(executableTwoParameters.getDeclaringType());
		assertNull(executableTwoParameters.getType());
		assertEquals(2, executableTwoParameters.getParameters().size());
		assertNotEquals(executableTwoParameters, executableZeroParameter);
		assertNotEquals(executableTwoParameters, executableOneParameter);
		assertEquals("Bar#m(int, java.lang.String)", executableTwoParameters.toString());
		assertEquals("new Bar().m(1, \"5\")", invocations.get(2).toString());

		// Static Executable reference.
		final CtExecutableReference<?> staticExecutable = references.get(3);
		assertNotNull(staticExecutable.getDeclaringType());
		assertNull(staticExecutable.getType());
		assertEquals(1, staticExecutable.getParameters().size());
		assertNotEquals(staticExecutable, executableZeroParameter);
		assertNotEquals(staticExecutable, executableOneParameter);
		assertEquals("Bar#m(java.lang.String)", staticExecutable.toString());
		assertEquals("Bar.m(\"42\")", invocations.get(3).toString());
	}

	@Test
	public void testSuperClassInGetAllExecutables() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/reference/testclasses/");
		launcher.setSourceOutputDirectory("./target/spoon-test");
		launcher.run();

		final CtClass<Burritos> aBurritos = launcher.getFactory().Class().get(Burritos.class);
		final CtMethod<?> aMethod = aBurritos.getMethodsByName("m").get(0);
		try {
			aMethod.getType().getAllExecutables();
		} catch (NullPointerException e) {
			fail("We shoudn't have a NullPointerException when we call getAllExecutables.");
		}
	}
}
