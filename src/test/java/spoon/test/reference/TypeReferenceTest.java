package spoon.test.reference;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResource;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.reference.testclasses.EnumValue;
import spoon.test.reference.testclasses.Panini;
import spoon.testing.utils.ModelUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.buildClass;
import static spoon.testing.utils.ModelUtils.canBeBuilt;
import static spoon.testing.utils.ModelUtils.createFactory;

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
		spoon.setArgs(new String[] {"--output-type", "nooutput" });
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
		if (fieldsOfB.size() == 2) {
			// Jacoco instruments all dependencies with an agent.
			// So, when we use reflection on ClassB, we don't have one field but two fields.
			// First, it is the field of ClassB. Second, it is the field of Jacoco.
			final CtFieldReference<?> potentialJacoco = (CtFieldReference<?>) fieldsOfB.toArray()[1];
			if ("$jacocoData".equals(potentialJacoco.getSimpleName())) {
				fieldsOfB.remove(potentialJacoco);
			}
		}
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

		// New type parameter declaration.
		final CtTypeParameter firstTypeParam = factory.Type().get(A.Tacos.class).getFormalCtTypeParameters().get(0);
		final CtTypeParameter secondTypeParam = factory.Type().get(B.Tacos.class).getFormalCtTypeParameters().get(0);
		assertNotEquals(firstTypeParam.toString(), secondTypeParam.toString());
		assertNotEquals(firstTypeParam, secondTypeParam);
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
		assertNotNull(genericType.getBoundingType());

		CtTypeReference<?> extendsGeneric = genericType.getBoundingType();
		assertNotNull(extendsGeneric);
		assertEquals(1, extendsGeneric.getActualTypeArguments().size());

		CtTypeParameterReference genericExtends = (CtTypeParameterReference) extendsGeneric.getActualTypeArguments().get(0);
		assertNotNull(genericExtends);
		assertNotNull(genericExtends.getBoundingType());

		assertTrue(genericExtends.getBoundingType() instanceof CtTypeReference);
	}

	@Test
	public void testRecursiveTypeReferenceInGenericType() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/reference/testclasses/EnumValue.java");
		launcher.setSourceOutputDirectory("./target/spoon-test");
		launcher.run();

		final CtClass<EnumValue> aClass = launcher.getFactory().Class().get(EnumValue.class);
		final CtMethod<?> asEnum = aClass.getMethodsByName("asEnum").get(0);

		// New type parameter declaration.
		final CtTypeParameter typeParameter = asEnum.getFormalCtTypeParameters().get(0);
		assertNotNull(typeParameter);
		assertNotNull(typeParameter.getSuperclass());

		final CtTypeReference<?> extendsGeneric = typeParameter.getSuperclass();
		assertNotNull(extendsGeneric);
		assertEquals(1, extendsGeneric.getActualTypeArguments().size());

		final CtTypeReference circularRef = extendsGeneric.getActualTypeArguments().get(0);
		assertNotNull(circularRef);
	}

	@Test
	public void testPackageInNoClasspath () {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/Demo.java");
		launcher.setSourceOutputDirectory("./target/class-declaration");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Object> aClass = launcher.getFactory().Class().get("Demo");
		final Set<CtTypeReference<?>> referencedTypes = aClass.getReferencedTypes();

		boolean containsDemoReference = false;
		boolean containsVoidReference = false;
		boolean containsStringReference = false;
		boolean containsJoinerReference = false;

		for (Iterator<CtTypeReference<?>> iterator = referencedTypes.iterator(); iterator.hasNext(); ) {
			CtTypeReference<?> reference = iterator.next();
			if (reference.toString().equals("Demo")) {
				containsDemoReference = true;
			} else if (reference.toString().equals("void")) {
				containsVoidReference = true;
			} else if (reference.toString().equals("java.lang.String")) {
				containsStringReference = true;
			} else if (reference.toString().equals("com.google.common.base.Joiner")) {
				containsJoinerReference = true;
			}
		}
		assertTrue("Reference to Demo is missing", containsDemoReference);
		assertTrue("Reference to void is missing", containsVoidReference);
		assertTrue("Reference to String is missing", containsStringReference);
		assertTrue("Reference to Joiner is missing", containsJoinerReference);
	}

	@Test
	public void testTypeReferenceSpecifiedInClassDeclarationInNoClasspath() throws Exception {
		// contract: Gets the import of a type specified in the declaration of a class.
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/Demo.java");
		launcher.setSourceOutputDirectory("./target/class-declaration");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Object> aClass = launcher.getFactory().Class().get("Demo");

		assertNotNull(aClass.getSuperclass());
		assertEquals("com.google.common.base.Function", aClass.getSuperclass().getQualifiedName());
		assertEquals(2, aClass.getSuperclass().getActualTypeArguments().size());
		assertEquals("java.lang.String", aClass.getSuperclass().getActualTypeArguments().get(0).toString());
		assertEquals("java.lang.String", aClass.getSuperclass().getActualTypeArguments().get(1).toString());

		assertEquals(1, aClass.getSuperInterfaces().size());
		for (CtTypeReference<?> superInterface : aClass.getSuperInterfaces()) {
			assertEquals("com.google.common.base.Function", superInterface.getQualifiedName());
			assertEquals(2, superInterface.getActualTypeArguments().size());
			assertEquals("java.lang.String", superInterface.getActualTypeArguments().get(0).toString());
			assertEquals("java.lang.String", superInterface.getActualTypeArguments().get(1).toString());
		}
	}

	@Test
	public void testTypeReferenceSpecifiedInClassDeclarationInNoClasspathWithGenerics() throws Exception {
		// contract: Gets the import of a type specified in the declaration of a class.
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/Demo2.java");
		launcher.setSourceOutputDirectory("./target/class-declaration");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Object> aClass = launcher.getFactory().Class().get("Demo2");
		Set<CtTypeReference<?>> superInterfaces = aClass.getSuperInterfaces();
		final CtTypeReference superInterface = superInterfaces.toArray(new CtTypeReference[superInterfaces.size()])[0];
		assertEquals("Bar", superInterface.getSimpleName());
		assertEquals(2, superInterface.getActualTypeArguments().size());
		final CtTypeReference<?> first = superInterface.getActualTypeArguments().get(0);
		assertTrue(first instanceof CtTypeParameterReference);
		assertEquals("?", first.getSimpleName());
		final CtTypeReference<?> second = superInterface.getActualTypeArguments().get(1);
		assertTrue(second instanceof CtTypeParameterReference);
		assertEquals("?", second.getSimpleName());

		// New.
		final CtTypeReference<?> bound = ((CtTypeParameterReference) second).getBoundingType();
		assertEquals("Tacos", bound.getSimpleName());
		assertEquals(1, bound.getActualTypeArguments().size());
		assertEquals("?", bound.getActualTypeArguments().get(0).getSimpleName());
		assertEquals("example.FooBar", superInterface.getDeclaringType().getQualifiedName());
		assertEquals("example.FooBar<?, ? extends Tacos<?>>.Bar<?, ? extends Tacos<?>>", superInterface.toString());
	}

	@Test
	public void testArgumentOfAInvocationIsNotATypeAccess() throws Exception {
		// contract: In no classpath, an unknown field specified as argument isn't a CtTypeAccess.
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/Demo3.java");
		launcher.setSourceOutputDirectory("./target/class-declaration");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Object> demo3 = launcher.getFactory().Class().get("Demo3");
		final List<CtFieldRead> fields = demo3.getElements(new TypeFilter<CtFieldRead>(CtFieldRead.class) {
			@Override
			public boolean matches(CtFieldRead element) {
				return "bar".equals(element.getVariable().getSimpleName()) && super.matches(element);
			}
		});
		assertEquals(1, fields.size());
	}

	@Test
	public void testInvocationWithFieldAccessInNoClasspath() throws Exception {
		// contract: In no classpath mode, if we have field accesses in an invocation, we should build field access and not type access.
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/Demo4.java");
		launcher.setSourceOutputDirectory("./target/class-declaration");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Object> demo4 = launcher.getFactory().Class().get("Demo4");
		final CtMethod<?> doSomething = demo4.getMethodsByName("doSomething").get(0);
		final CtInvocation topInvocation = doSomething.getElements(new TypeFilter<>(CtInvocation.class)).get(0);
		assertNotNull(topInvocation.getTarget());
		assertTrue(topInvocation.getTarget() instanceof CtInvocation);
		assertNotNull(((CtInvocation) topInvocation.getTarget()).getTarget());
		assertTrue(((CtInvocation) topInvocation.getTarget()).getTarget() instanceof CtFieldRead);
		assertEquals(1, topInvocation.getArguments().size());
		assertTrue(topInvocation.getArguments().get(0) instanceof CtFieldRead);
		assertEquals("a.foo().bar(b)", topInvocation.toString());

		// Class concerned by this bug.
		canBeBuilt("./src/test/resources/noclasspath/TestBot.java", 8, true);
	}

	@Test
	public void testAnnotationOnMethodWithPrimitiveReturnTypeInNoClasspath() throws Exception {
		// contract: In no classpath mode, if we have an annotation declared on a method and overridden
		// from a super class in an anonymous class, we should rewrite correctly the annotation and don't
		// throw a NPE.
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/A.java");
		launcher.setSourceOutputDirectory("./target/class-declaration");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Object> aClass = launcher.getFactory().Class().get("A");
		final CtClass anonymousClass = aClass.getElements(new TypeFilter<>(CtNewClass.class)).get(0).getAnonymousClass();
		final CtMethod run = anonymousClass.getMethod("run");
		assertNotNull(run);
		assertEquals(1, run.getAnnotations().size());
		assertEquals("@java.lang.Override" + System.lineSeparator(), run.getAnnotations().get(0).toString());
	}

	@Test
	public void testAnonymousClassesHaveAnEmptyStringForItsNameInNoClasspath() throws Exception {
		// contract: In no classpath mode, a type reference have an empty string for its name.
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/A.java");
		launcher.setSourceOutputDirectory("./target/class-declaration");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Object> aClass = launcher.getFactory().Class().get("A");
		final CtClass anonymousClass = aClass.getElements(new TypeFilter<>(CtNewClass.class)).get(0).getAnonymousClass();
		assertEquals("1", anonymousClass.getReference().getSimpleName());
		Set<CtTypeReference<?>> referencedTypes = aClass.getReferencedTypes();
		List<String> referencedTypeNames = referencedTypes.stream().map(Object::toString)
				.collect(Collectors.toList());
		assertEquals(7, referencedTypeNames.size());
		assertTrue(referencedTypeNames.contains("A"));
		assertTrue(referencedTypeNames.contains("example.B"));
		assertTrue(referencedTypeNames.contains("java.lang.Runnable"));
		assertTrue(referencedTypeNames.contains("java.lang.Override"));
		assertTrue(referencedTypeNames.contains("java.lang.Object"));
		assertTrue(referencedTypeNames.contains("A.1"));
		assertTrue(referencedTypeNames.contains("void"));
	}

	@Test
	public void testConstructorCallInNoClasspath() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/resources/noclasspath/Demo5.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Object> demo5 = launcher.getFactory().Class().get("Demo5");
		final CtMethod<Object> foo = demo5.getMethod("foo");
		final List<CtConstructorCall> elements = foo.getElements(new TypeFilter<>(CtConstructorCall.class));

		assertEquals("A.B<C>", elements.get(0).getType().toString());
		assertEquals("D", elements.get(1).getType().toString());
	}

	@Test
	public void testShortTypeReference() throws Exception {

		CtTypeReference<Short> aShort = ModelUtils.createFactory().Type().SHORT;
		CtTypeReference<Short> shortPrimitive = ModelUtils.createFactory().Type().SHORT_PRIMITIVE;

		assertEquals(Short.class, aShort.getActualClass());
		assertEquals(short.class, shortPrimitive.getActualClass());

	}

	@Test
	public void testClearBoundsForTypeParameterReference() throws Exception {
		final Factory factory = createFactory();
		final CtTypeParameterReference reference = factory.Type().createTypeParameterReference("T");
		reference.addBound(factory.Type().createReference(String.class));

		assertNotNull(reference.getBoundingType());

		reference.setBounds(null);

		assertNull(reference.getBoundingType());

		reference.addBound(factory.Type().createReference(String.class));

		assertNotNull(reference.getBoundingType());

		reference.setBounds(new ArrayList<>());

		assertNull(reference.getBoundingType());
	}

	@Test(expected = SpoonException.class)
	public void testReferenceName() throws Exception {
		final Factory factory = createFactory();
		final CtTypeReference<Object> typeReference = factory.Core().createTypeReference();
		typeReference.setSimpleName("?");
	}

	@Test
	public void testIgnoreEnclosingClassInActualTypes() throws Exception {
		final CtType<Panini> aPanini = buildClass(Panini.class);
		final CtStatement ctReturn = aPanini.getMethod("entryIterator").getBody().getStatement(0);
		assertTrue(ctReturn instanceof CtReturn);
		final CtExpression ctConstructorCall = ((CtReturn) ctReturn).getReturnedExpression();
		assertTrue(ctConstructorCall instanceof CtConstructorCall);
		assertEquals("spoon.test.reference.testclasses.Panini<K, V>.Itr<java.util.Map.Entry<K, V>>", ctConstructorCall.getType().toString());
	}

	class A {
		class Tacos<K> {
		}
	}

	class B {
		class Tacos<K extends A> {
		}
	}

	@Test
	public void testCorrectEnumParent() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();
		CtEnum e = launcher.getFactory().Enum().create("spoon.test.reference.EnumE");
		CtTypeReference correctParent = launcher.getFactory().Type().createReference(java.lang.Enum.class);
		assertEquals(correctParent, e.getReference().getSuperclass());
	}

	@Test
	public void testImproveAPIActualTypeReference() throws Exception {
		final Factory factory = createFactory();
		List<CtTypeParameterReference> typeParameterReferences = new ArrayList<>();
		typeParameterReferences.add(factory.Type().createTypeParameterReference("Foo"));
		final CtTypeReference<Object> typeReference = factory.Core().createTypeReference();
		typeReference.setActualTypeArguments(typeParameterReferences);

		assertEquals(1, typeReference.getActualTypeArguments().size());
	}

	@Test
	public void testIsSubTypeSuperClassNull() throws Exception {
		Factory factory = createFactory();

		factory.Class().create("Tacos");
		CtTypeReference<?> subRef = factory.Type().createReference(AutoCloseable.class);
		CtTypeReference<?> superRef = factory.Type().createReference("Tacos");

		assertFalse(subRef.isSubtypeOf(superRef));
	}

	@Test
	public void testSubTypeAnonymous() throws Exception {
		CtType<Panini> paniniCtType = buildClass(Panini.class);

		CtClass anonymousClass = ((CtNewClass) ((CtReturn) paniniCtType
				.getMethod("entryIterator").getBody().getStatement(0))
				.getReturnedExpression()).getAnonymousClass();

		assertTrue(anonymousClass.getReference().isSubtypeOf(paniniCtType.getFactory().Type().createReference("spoon.test.reference.testclasses.Panini$Itr")));
	}

	@Test
	public void testGetTypeDeclaration() throws Exception {
		Launcher l = new Launcher();
		l.addInputResource("src/test/resources/compilation/compilation-tests/");
		l.buildModel();
		CtType<?> bar = l.getFactory().Type().get("compilation.Bar");
		CtType iBar = bar.getSuperInterfaces().toArray(new CtTypeReference[0])[0].getTypeDeclaration();
		assertNotNull(iBar);
		assertEquals("compilation.IBar", iBar.getQualifiedName());
	}
}
