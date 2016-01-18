package spoon.test.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Test;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.reference.SpoonClassNotFoundException;
import spoon.support.visitor.SignaturePrinter;
import spoon.test.api.testclasses.Bar;

public class NoClasspathTest {

	@Test
	public void test() throws Exception {
		// do we still have a correct model when the complete classpath is not given as input?
		Launcher spoon = new Launcher();
		spoon.getEnvironment().setNoClasspath(true);
		spoon.getEnvironment().setLevel("OFF");
		spoon.addInputResource("./src/test/resources/spoon/test/noclasspath/fields");
		spoon.getEnvironment().getDefaultFileGenerator().setOutputDirectory(new File("target/spooned/apitest"));
		spoon.run();
		Factory factory = spoon.getFactory();
		CtClass<Object> clazz = factory.Class().get("Foo");

		assertEquals("Foo", clazz.getSimpleName());
		CtTypeReference<?> superclass = clazz.getSuperclass();
		// "Unknown" is not in the classpath at all
		assertEquals("Unknown", superclass.getSimpleName());
		try {
			superclass.getActualClass();
			fail();
		} catch (SpoonClassNotFoundException e) {
			// expected
		}
		assertNull(superclass.getDeclaration());

		// now we really make sure we don't have the class in the classpath
		try {
			superclass.getActualClass();
			fail();
		} catch (SpoonException e) {}

		{
			CtMethod<?> method = clazz.getMethod("method", new CtTypeReference[0]);
			assertNotNull(method);
			List<CtInvocation<?>> invocations = method.getElements(new TypeFilter<CtInvocation<?>>(CtInvocation.class));
			assertEquals(1, invocations.size());
			CtInvocation<?> c = invocations.get(0);
			assertEquals("method", c.getExecutable().getSimpleName());
			assertEquals("x.method()", method.getBody().getStatement(1).toString());
		}

		{
			CtMethod<?> method = clazz.getMethod("m2", new CtTypeReference[0]);
			assertNotNull(method);
			List<CtInvocation<?>> invocations = method.getElements(new TypeFilter<CtInvocation<?>>(CtInvocation.class));
			assertEquals(3, invocations.size());
			CtInvocation<?> c = invocations.get(1);
			assertEquals("second", c.getExecutable().getSimpleName());
			assertEquals("x.first().second().third()", method.getBody().getStatement(1).toString());
		}

		{
			CtMethod<?> method = clazz.getMethod("m1", new CtTypeReference[0]);
			assertNotNull(method);
			List<CtInvocation<?>> invocations = method.getElements(new TypeFilter<CtInvocation<?>>(CtInvocation.class));
			assertEquals(1, invocations.size());
			invocations.get(0);
			assertEquals("x.y.z.method()", method.getBody().getStatement(0).toString());
		}

		{
			CtMethod<?> method = clazz.getMethod("m3",new CtTypeReference[0]);
			assertNotNull(method);
			List<CtInvocation<?>> invocations = method.getElements(new TypeFilter<CtInvocation<?>>(CtInvocation.class));
			assertEquals(1, invocations.size());
			invocations.get(0);
			CtLocalVariable<?> statement = method.getBody().getStatement(0);
			CtFieldAccess<?>  fa = (CtFieldAccess<?>) statement.getDefaultExpression();
			assertTrue(fa.getTarget() instanceof CtInvocation);
			assertEquals("field", fa.getVariable().getSimpleName());
			assertEquals("int x = first().field", statement.toString());
		}

	}

	@Test
	public void testBug20141021() {
		// 2014/10/21 NPE is noclasspath mode on a large open-source project

		Launcher spoon = new Launcher();
		Factory f = spoon.getFactory();
		CtExecutableReference<Object> ref = f.Core().createExecutableReference();
		ref.setSimpleName("foo");

		SignaturePrinter pr = new SignaturePrinter();
		pr.scan(ref);
		String s = pr.getSignature();

		assertEquals("#foo()", s);
	}

	@Test
	public void testGetStaticDependency() {
		Launcher spoon = new Launcher();
		final Factory factory = spoon.getFactory();
		factory.getEnvironment().setAutoImports(false);
		spoon.addInputResource("./src/test/java/spoon/test/api/testclasses/");
		spoon.getEnvironment().getDefaultFileGenerator().setOutputDirectory(new File("target/spooned/apitest"));
		spoon.run();

		CtTypeReference<?> expectedType = factory.Type().createReference(javax.sound.sampled.AudioFormat.Encoding.class);
		CtClass<?> clazz = factory.Class().get(Bar.class);

		CtMethod<?> method = clazz.getMethodsByName("doSomething").get(0);
		CtReturn<?> ctReturn = method.getElements(new TypeFilter<CtReturn<?>>(CtReturn.class)).get(0);

		assertEquals(true, ctReturn.getReferencedTypes().contains(expectedType));
	}

}
