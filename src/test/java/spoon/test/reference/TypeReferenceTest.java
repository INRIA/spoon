package spoon.test.reference;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;

/**
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class TypeReferenceTest {

	@Test
	public void testGetAllExecutablesForInterfaces() throws Exception {
		
		/*
		 * This test has been written because getAllExecutables wasn't recursing
		 * into the type hierarchy for interfaces.
		 */
		
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		spoon.createCompiler(
			factory,
			SpoonResourceHelper
				.resources("./src/test/java/spoon/test/reference/Foo.java"))
			.build();
		
		CtInterface<Foo> foo =
			factory.Package().get("spoon.test.reference").getType("Foo");
		Collection<CtExecutableReference<?>> execs =
			foo.getReference().getAllExecutables();
		
		assertEquals(2,execs.size());
	}
	
	
}
