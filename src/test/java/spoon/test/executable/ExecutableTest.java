package spoon.test.executable;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.executable.testclasses.A;
import spoon.test.executable.testclasses.Pozole;
import spoon.testing.utils.ModelUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExecutableTest {
	@Test
	public void testInfoInsideAnonymousExecutable() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/executable/testclasses/AnonymousExecutableSample.java");
		launcher.run();

		final List<CtAnonymousExecutable> anonymousExecutables = Query.getElements(launcher.getFactory(), new TypeFilter<CtAnonymousExecutable>(CtAnonymousExecutable.class));

		assertEquals(2, anonymousExecutables.size());

		for (CtAnonymousExecutable anonymousExecutable : anonymousExecutables) {
			assertEquals("", anonymousExecutable.getSimpleName());
			assertEquals(launcher.getFactory().Type().VOID_PRIMITIVE, anonymousExecutable.getType());
			assertEquals(0, anonymousExecutable.getParameters().size());
			assertEquals(0, anonymousExecutable.getThrownTypes().size());
		}
	}

	@Test
	public void testBlockInExecutable() throws Exception {
		final CtType<Pozole> aPozole = ModelUtils.buildClass(Pozole.class);
		assertTrue(aPozole.getMethod("m").getBody().getStatement(1) instanceof CtBlock);
	}

	@Test
	public void testGetReference() throws Exception {
		final CtType<A> aClass = ModelUtils.buildClass(A.class);

		String methodName = "getInt1";
		CtExecutableReference<?> methodRef = aClass.getMethod(methodName).getReference();
		assertEquals(false, methodRef.isFinal());
		assertEquals(true, methodRef.isStatic());
		assertEquals(aClass.getFactory().Type().integerPrimitiveType(), methodRef.getType());
		assertEquals(aClass.getMethod(methodName), methodRef.getDeclaration());

		methodName = "getInt2";
		methodRef = aClass.getMethod(methodName).getReference();
		assertEquals(true, methodRef.isFinal());
		assertEquals(true, methodRef.isStatic());
		assertEquals(aClass.getFactory().Type().integerPrimitiveType(), methodRef.getType());
		assertEquals(aClass.getMethod(methodName), methodRef.getDeclaration());

		methodName = "getInt3";
		methodRef = aClass.getMethod(methodName).getReference();
		assertEquals(true, methodRef.isFinal());
		assertEquals(false, methodRef.isStatic());
		assertEquals(aClass.getFactory().Type().integerPrimitiveType(), methodRef.getType());
		assertEquals(aClass.getMethod(methodName), methodRef.getDeclaration());

		methodName = "getInt4";
		methodRef = aClass.getMethod(methodName).getReference();
		assertEquals(false, methodRef.isFinal());
		assertEquals(false, methodRef.isStatic());
		assertEquals(aClass.getFactory().Type().integerPrimitiveType(), methodRef.getType());
		assertEquals(aClass.getMethod(methodName), methodRef.getDeclaration());
	}
}
