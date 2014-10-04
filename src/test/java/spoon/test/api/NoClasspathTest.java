package spoon.test.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
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
		
		// now we see whether we have a method for which there is no declaration
		// we take the second invocation
		// because the first one is hidden in the implicit default constructor
		CtInvocation<?> c = clazz.getElements(new TypeFilter<CtInvocation>(CtInvocation.class)).get(1);
		assertEquals("method", c.getExecutable().getSimpleName());
	}
	
}
