package spoon.test.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Test;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.FileSystemFolder;

public class NoClasspathTest {

	@Test
	public void test() throws Exception {
		// do we still have a correct model when the complete classpath is not given as input?
		Launcher spoon = new Launcher();
		spoon.getFactory().getEnvironment().setNoClasspath(true);
		spoon.addInputResource(new FileSystemFolder(new File("./src/test/resources/spoon/test/noclasspath")));
		spoon.run();
		Factory factory = spoon.getFactory();
		CtClass<Object> clazz = factory.Class().get("Foo"); 
		
		assertEquals("Foo", clazz.getSimpleName());
		CtTypeReference<?> superclass = clazz.getSuperclass();
		// "Unknown" is not in the classpath at all
		assertEquals("Unknown", superclass.getSimpleName());
		assertNull(superclass.getDeclaration());
		
		// now we really make sure we don't have the class in the classpath
		try {
			superclass.getActualClass();
			fail(); 
		} catch (SpoonException e) {} 
		
		{
			CtMethod method = clazz.getMethod("method", new CtTypeReference[0]);
			assertNotNull(method);
			List<CtInvocation> invocations = method.getElements(new TypeFilter<CtInvocation>(CtInvocation.class));
			assertEquals(1, invocations.size());
			CtInvocation<?> c = invocations.get(0);
			assertEquals("method", c.getExecutable().getSimpleName());
			assertEquals("x.method()", method.getBody().getStatement(1).toString());
		}
		
		{
			CtMethod method = clazz.getMethod("m2", new CtTypeReference[0]);
			assertNotNull(method);
			List<CtInvocation> invocations = method.getElements(new TypeFilter<CtInvocation>(CtInvocation.class));
			assertEquals(3, invocations.size());
			CtInvocation<?> c = invocations.get(1);
			assertEquals("second", c.getExecutable().getSimpleName());
			assertEquals("x.first().second().third()", method.getBody().getStatement(1).toString());
		}

		{
			CtMethod method = clazz.getMethod("m1", new CtTypeReference[0]);
			assertNotNull(method);
			List<CtInvocation> invocations = method.getElements(new TypeFilter<CtInvocation>(CtInvocation.class));
			assertEquals(1, invocations.size());
			CtInvocation<?> c = invocations.get(0);
			assertEquals("x.y.z.method()", method.getBody().getStatement(0).toString());
		}
	}
	
}
