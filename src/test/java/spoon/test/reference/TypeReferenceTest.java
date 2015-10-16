package spoon.test.reference;

import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResource;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.internal.CtCircularTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Lionel Seinturier
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
		spoon.createCompiler(factory, SpoonResourceHelper.resources("./src/test/java/spoon/test/reference/Foo.java")).build();

		CtInterface<Foo> foo = factory.Package().get("spoon.test.reference").getType("Foo");
		Collection<CtExecutableReference<?>> execs = foo.getReference().getAllExecutables();

		assertEquals(2, execs.size());
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
		CtType<?> theClass = factory.Type().get(qualifiedName);

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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void doNotCloseLoader() throws Exception {

		/* Given the following scenario:
		 * 	- ClassA has a field of ClassB.
		 *	- ClassB has a field of ClassC.
		 * 	- Spoon only models ClassA.
		 *
		 * We want to get the field of ClassB, which should be accessible because
		 * the definitions of ClassB and ClassC were provided in the class path.
		 */

		SpoonCompiler comp = new Launcher().createCompiler();
		Factory factory = comp.getFactory();

		String qualifiedName = "spoontest.a.ClassA";
		String referenceQualifiedName = "spoontest.b.ClassB";

		// we only create the model for ClassA
		List<SpoonResource> fileToBeSpooned = SpoonResourceHelper.resources("./src/test/resources/reference-test-2/" + qualifiedName.replace('.', '/') + ".java");
		comp.addInputSources(fileToBeSpooned);
		assertEquals(1, fileToBeSpooned.size()); // for ClassA

		// Spoon requires the binary version of dependencies
		List<SpoonResource> classpath = SpoonResourceHelper.resources("./src/test/resources/reference-test-2/ReferenceTest2.jar");
		String[] dependencyClasspath = new String[] { classpath.get(0).getPath() };

		factory.getEnvironment().setSourceClasspath(dependencyClasspath);
		assertEquals(1, classpath.size());

		// now we can build the model
		comp.build();

		// we can get the model of ClassA
		CtType<?> theClass = factory.Type().get(qualifiedName);

		// we get ClassA's field of type ClassB
		List<CtField<?>> fields = theClass.getFields();
		assertEquals(1, fields.size());

		CtField<?> bField = fields.get(0);
		CtTypeReference referencedType = bField.getType();
		assertEquals(referenceQualifiedName, referencedType.getQualifiedName());

		// we get ClassB's field of type ClassC
		Collection<CtFieldReference<?>> fieldsOfB = referencedType.getAllFields();
		assertEquals(1, fieldsOfB.size());

		CtFieldReference<?> cField = fieldsOfB.iterator().next();
		assertEquals("spoontest.c.ClassC", cField.getType().getQualifiedName());
	}

	@Test
	public void testNullReferenceSubtype() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();

		CtTypeReference<?> ref = factory.Type().createReference(String.class);
		CtTypeReference<?> nullRef = factory.Type().createReference(CtTypeReference.NULL_TYPE_NAME);

		assertFalse(ref.isSubtypeOf(nullRef));
		assertFalse(nullRef.isSubtypeOf(ref));

	}

	@Test
	public void unboxTest() {
		Factory factory = new Launcher().createFactory();
		CtTypeReference<Boolean> boxedBoolean = factory.Class().createReference(Boolean.class);
		assertEquals(boxedBoolean.unbox().getActualClass(), boolean.class);
	}

	@Test
	public void testToStringEqualityBetweenTwoGenericTypeDifferent() throws Exception {
		// contract: generic type references with different bounds should not be considered equals
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput", "--noclasspath"});
		launcher.addInputResource("src/test/java/spoon/test/reference/TypeReferenceTest.java");
		launcher.run();

		Factory factory = launcher.getFactory();
		final CtTypeReference<?> firstRef = factory.Type().get(A.Tacos.class).getFormalTypeParameters().get(0);
		final CtTypeReference<?> secondRef = factory.Type().get(B.Tacos.class).getFormalTypeParameters().get(0);

		assertNotEquals(firstRef.toString(), secondRef.toString());
		assertNotEquals(firstRef, secondRef);
	}

	@Test
	public void testRecursiveTypeReference() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/reference/testclasses/Tacos.java");
		launcher.setSourceOutputDirectory("./target/spoon-test");
		launcher.run();

		final CtInvocation<?> inv = Query.getElements(launcher.getFactory(), new TypeFilter<CtInvocation<?>>(CtInvocation.class) {
			@Override
			public boolean matches(CtInvocation<?> element) {
				return !element.getExecutable().isConstructor() && super.matches(element);
			}
		}).get(0);

		assertNotNull(inv.getExecutable());
		final CtTypeReference<?> returnType = inv.getExecutable().getType();
		assertNotNull(returnType);
		assertEquals(1, returnType.getActualTypeArguments().size());

		final CtTypeParameterReference genericType = (CtTypeParameterReference) returnType.getActualTypeArguments().get(0);
		assertNotNull(genericType);
		assertEquals(1, genericType.getBounds().size());

		final CtTypeReference<?> extendsGeneric = genericType.getBounds().get(0);
		assertNotNull(extendsGeneric);
		assertEquals(1, extendsGeneric.getActualTypeArguments().size());

		final CtTypeParameterReference genericExtends = (CtTypeParameterReference) extendsGeneric.getActualTypeArguments().get(0);
		assertNotNull(genericExtends);
		assertEquals(1, genericExtends.getBounds().size());

		final CtTypeReference<?> circularRef = genericExtends.getBounds().get(0);
		assertTrue(circularRef instanceof CtCircularTypeReference);
	}

	class A {
		class Tacos<K> {
		}
	}

	class B {
		class Tacos<K extends A> {
		}
	}
}
