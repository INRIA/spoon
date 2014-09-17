package spoon.test.reference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResource;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;

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
	
	@SuppressWarnings("rawtypes")
	@Test
	public void loadReferencedClassFromClasspath() throws Exception {
		SpoonCompiler comp = new Launcher().createCompiler();
		Factory factory = comp.getFactory();

		String packageName = "spoon.test.reference";
		String className = "ReferencingClass";
		String qualifiedName = packageName + "." + className;
		String referencedQualifiedName = packageName + "." + "ReferencedClass";
		
		// we only create the model for ReferecingClass
		List<SpoonResource> fileToBeSpooned = SpoonResourceHelper.resources("./src/test/resources/reference-test/input/" + qualifiedName.replace('.', '/') + ".java");
		comp.addInputSources(fileToBeSpooned);
		assertEquals(1, fileToBeSpooned.size()); // for ReferecingClass

		// Spoon requires the binary version of ReferencedClass
		List<SpoonResource> classpath = SpoonResourceHelper.resources("./src/test/resources/reference-test/ReferenceTest.jar");
		String[] dependencyClasspath = new String[] { classpath.get(0).getPath() };
		
		factory.getEnvironment().setSourceClasspath(dependencyClasspath);
		assertEquals(1, classpath.size());

		// now we can build the model
		comp.build();

		// we can get the model of ReferecingClass
		CtSimpleType<?> theClass = factory.Type().get(qualifiedName);

		// now we retrieve the reference to ReferencedClass
		CtTypeReference referencedType = null;
		ReferenceTypeFilter<CtTypeReference> referenceTypeFilter = new ReferenceTypeFilter<CtTypeReference>(CtTypeReference.class);
		List<CtTypeReference> elements = Query.getReferences(theClass, referenceTypeFilter);
		for (CtTypeReference reference : elements) {
			if (reference.getQualifiedName().equals(referencedQualifiedName)) {
				referencedType = reference;
				break;
			}
		}
		assertFalse(referencedType == null);
		
		// we can get the actual class from the reference, because it is loaded from the class path
		Class referencedClass = referencedType.getActualClass();
		assertEquals(referencedQualifiedName, referencedClass.getName());
	}
}
