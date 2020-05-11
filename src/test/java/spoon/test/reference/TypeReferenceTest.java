/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.test.reference;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResource;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.reference.testclasses.EnumValue;
import spoon.test.reference.testclasses.Panini;
import spoon.test.reference.testclasses.ParamRefs;
import spoon.test.reference.testclasses.SuperAccess;
import spoon.testing.utils.ModelUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
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
		SpoonModelBuilder comp = new Launcher().createCompiler();
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
		String[] dependencyClasspath = { classpath.get(0).getPath() };

		factory.getEnvironment().setSourceClasspath(dependencyClasspath);
		assertEquals(1, classpath.size());

		// now we can build the model
		comp.build();

		// we can get the model of ReferecingClass
		CtType<?> theClass = factory.Type().get(qualifiedName);

		// now we retrieve the reference to ReferencedClass
		CtTypeReference referencedType = null;
		ReferenceTypeFilter<CtTypeReference> referenceTypeFilter = new ReferenceTypeFilter<>(CtTypeReference.class);
		List<CtTypeReference> elements = Query.getElements(theClass, referenceTypeFilter);
		for (CtTypeReference reference : elements) {
			if (reference.getQualifiedName().equals(referencedQualifiedName)) {
				referencedType = reference;
				break;
			}
		}
		assertNotNull(referencedType);

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

		SpoonModelBuilder comp = new Launcher().createCompiler();
		Factory factory = comp.getFactory();

		String qualifiedName = "spoontest.a.ClassA";
		String referenceQualifiedName = "spoontest.b.ClassB";

		// we only create the model for ClassA
		List<SpoonResource> fileToBeSpooned = SpoonResourceHelper.resources("./src/test/resources/reference-test-2/" + qualifiedName.replace('.', '/') + ".java");
		comp.addInputSources(fileToBeSpooned);
		assertEquals(1, fileToBeSpooned.size()); // for ClassA

		// Spoon requires the binary version of dependencies
		List<SpoonResource> classpath = SpoonResourceHelper.resources("./src/test/resources/reference-test-2/ReferenceTest2.jar");
		String[] dependencyClasspath = { classpath.get(0).getPath() };

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
	public void testNullReferenceSubtype() {
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
		assertSame(boolean.class, boxedBoolean.unbox().getActualClass());
	}

	@Test
	public void testToStringEqualityBetweenTwoGenericTypeDifferent() {
		// contract: generic type references with different bounds should not be considered equals
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput"});
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
	public void testRecursiveTypeReference() {
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
	public void testRecursiveTypeReferenceInGenericType() {
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
	public void testUnknownSuperClassWithSameNameInNoClasspath() {
		// contract: Gets the import of a type specified in the declaration of a class.
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/Attachment.java");
		launcher.setSourceOutputDirectory("./target/class-declaration");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		CtClass<?> ctType = (CtClass<?>) launcher.getFactory().Class().getAll().get(0);
		assertNotEquals(ctType.getSuperclass(), ctType.getReference());
		assertEquals("it.feio.android.omninotes.commons.models.Attachment", ctType.getSuperclass().toString());
		assertEquals("it.feio.android.omninotes.models.Attachment", ctType.getReference().toString());

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

		for (CtTypeReference<?> reference : referencedTypes) {
			if ("Demo".equals(reference.toString())) {
				containsDemoReference = true;
			} else if ("void".equals(reference.toString())) {
				containsVoidReference = true;
			} else if ("java.lang.String".equals(reference.toString())) {
				containsStringReference = true;
			} else if ("com.google.common.base.Joiner".equals(reference.toString())) {
				containsJoinerReference = true;
			}
		}
		assertTrue("Reference to Demo is missing", containsDemoReference);
		assertTrue("Reference to void is missing", containsVoidReference);
		assertTrue("Reference to String is missing", containsStringReference);
		assertTrue("Reference to Joiner is missing", containsJoinerReference);
	}

	@Test
	public void testTypeReferenceSpecifiedInClassDeclarationInNoClasspath() {
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
	public void testTypeReferenceSpecifiedInClassDeclarationInNoClasspathWithGenerics() {
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
	public void testArgumentOfAInvocationIsNotATypeAccess() {
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
	public void testInvocationWithFieldAccessInNoClasspath() {
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
	public void testAnnotationOnMethodWithPrimitiveReturnTypeInNoClasspath() {
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
		assertEquals("@java.lang.Override", run.getAnnotations().get(0).toString());
	}

	@Test
	public void testAnonymousClassesHaveAnEmptyStringForItsNameInNoClasspath() {
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
	public void testConstructorCallInNoClasspath() {
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
	public void testShortTypeReference() {

		CtTypeReference<Short> aShort = createFactory().Type().SHORT;
		CtTypeReference<Short> shortPrimitive = createFactory().Type().SHORT_PRIMITIVE;

		assertSame(Short.class, aShort.getActualClass());
		assertSame(short.class, shortPrimitive.getActualClass());

	}

	@Test
	public void testClearBoundsForWildcardReference() {
		final Factory factory = createFactory();
		final CtWildcardReference reference = factory.createWildcardReference();
		reference.setBoundingType(factory.Type().createReference(String.class));

		assertEquals(factory.Type().STRING, reference.getBoundingType());

		reference.setBoundingType(null);

		assertEquals(factory.Type().OBJECT, reference.getBoundingType());
		assertTrue(reference.isDefaultBoundingType());

		reference.setBoundingType(factory.Type().createReference(String.class));

		assertEquals(factory.Type().STRING, reference.getBoundingType());

		reference.setBoundingType(factory.Type().objectType());

		assertEquals(factory.Type().OBJECT, reference.getBoundingType());
		assertTrue(reference.isDefaultBoundingType());
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
	public void testImproveAPIActualTypeReference() {
		final Factory factory = createFactory();
		List<CtTypeParameterReference> typeParameterReferences = new ArrayList<>();
		typeParameterReferences.add(factory.Type().createTypeParameterReference("Foo"));
		final CtTypeReference<Object> typeReference = factory.Core().createTypeReference();
		typeReference.setActualTypeArguments(typeParameterReferences);

		assertEquals(1, typeReference.getActualTypeArguments().size());
	}

	@Test
	public void testIsSubTypeSuperClassNull() {
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
	public void testGetTypeDeclaration() {
		Launcher l = new Launcher();
		l.addInputResource("src/test/resources/compilation/compilation-tests/");
		l.buildModel();
		CtType<?> bar = l.getFactory().Type().get("compilation.Bar");
		CtType iBar = bar.getSuperInterfaces().toArray(new CtTypeReference[0])[0].getTypeDeclaration();
		assertNotNull(iBar);
		assertEquals("compilation.IBar", iBar.getQualifiedName());
	}

	@Test
	public void testTypeDeclarationWildcard() {
		// contract1: getTypeDeclaration nevers returns null, even for wilddards
		// contract2: getTypeDeclaration returns a CtTYpe representing Object as the compiler does
		CtLocalVariable<?> s = new Launcher().getFactory().Code().createCodeSnippetStatement("java.util.List<?> l = null").compile();
		assertEquals("?", s.getType().getActualTypeArguments().get(0).getSimpleName());
		assertTrue(s.getType().getActualTypeArguments().get(0) instanceof CtWildcardReference);
		assertEquals("Object", s.getType().getActualTypeArguments().get(0).getTypeDeclaration().getSimpleName());
		assertSame(Object.class, s.getType().getActualTypeArguments().get(0).getTypeDeclaration().getActualClass());

		// some additional tests
		CtLocalVariable<?> s2 = new Launcher().getFactory().Code().createCodeSnippetStatement("java.util.List<String> l = null").compile();
		assertEquals("String", s2.getType().getActualTypeArguments().get(0).getSimpleName());
		assertSame(String.class, s2.getType().getActualTypeArguments().get(0).getTypeDeclaration().getActualClass());
	}

	@Test
	public void testEqualityTypeReference() throws Exception {
		CtClass<ParamRefs> aClass = (CtClass) buildClass(ParamRefs.class);
		CtParameter<?> parameter = aClass.getElements(new NamedElementFilter<>(CtParameter.class,"param")).get(0);
		CtParameterReference<?> parameterRef1 = parameter.getReference();
		CtParameterReference<?> parameterRef2 = aClass.getElements((CtParameterReference<?> ref)-> "param".equals(ref.getSimpleName())).get(0);

		// fresh reference not put in a context
		assertNull(parameterRef1.getDeclaringExecutable());
		assertEquals(aClass.getReference(), parameterRef2.getDeclaringExecutable().getType());

		assertEquals(parameterRef1, parameterRef2);
	}

	@Test
	public void testTypeReferenceWithGenerics() {
		// contract: in noclasspath, a generic type name should not contain generic information
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/import-with-generics/TestWithGenerics.java");
		launcher.getEnvironment().setAutoImports(true);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		CtField field = launcher.getModel().getElements(new TypeFilter<>(CtField.class)).get(0);
		CtTypeReference fieldTypeRef = field.getType();

		assertEquals("spoon.test.imports.testclasses.withgenerics.Target", fieldTypeRef.getQualifiedName());
		assertEquals(2, fieldTypeRef.getActualTypeArguments().size());
	}
	
	@Test
	public void testTypeReferenceImplicitParent() throws Exception {
		// contract: CtTypeReference#isSimplyQualified can be used read / write implicit value of the parent
		CtType<?> type = ModelUtils.buildClass(SuperAccess.class);
		CtTypeReference<?> typeRef = type.getSuperclass();
		assertTrue(typeRef.isSimplyQualified());
		assertTrue(typeRef.getPackage().isImplicit());
		
		// a type reference can be printed fully qualified
		typeRef.setSimplyQualified(false);
		assertFalse(typeRef.isSimplyQualified());
		assertFalse(typeRef.getPackage().isImplicit());
		assertEquals("spoon.test.reference.testclasses.Parent", typeRef.toStringDebug());

		// a type reference can be printed simply qualified
		typeRef.getPackage().setImplicit(true);
		assertTrue(typeRef.isSimplyQualified());
		assertTrue(typeRef.getPackage().isImplicit());
		assertEquals("spoon.test.reference.testclasses.Parent", typeRef.toString());
	}

	@Test
	public void testIsInTheSamePackageNoClasspath() {
		// contract: we should not get NPE within canAccess() in noclasspath mode
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/A5.java");
		launcher.getEnvironment().setNoClasspath(true);
		CtModel model = launcher.buildModel();
		CtType<?> type = model.getAllTypes().stream().findFirst().get();

		List<CtLocalVariable> vars = type.getElements(new TypeFilter<>(CtLocalVariable.class));
		List<CtField> fields = type.getElements(new TypeFilter<>(CtField.class));
		List<CtInvocation> invocations = type.getElements(new TypeFilter<>(CtInvocation.class));

		// test null type
		CtTypeReference tr1 = invocations.get(1).getType();
		assertTrue(fields.get(0).getReference().getType().canAccess(tr1));

		// test type with null package
		CtTypeReference tr2 = fields.get(0).getType();
		assertTrue(vars.get(0).getReference().getType().canAccess(tr2));
	}

	@Test
	public void testQualifiedArrayTypeReferenceNoClasspath() {
		// contract: component type of explicitly qualified array type reference should have explicit package reference
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/QualifiedArrayType.java");
		launcher.getEnvironment().setNoClasspath(true);
		CtModel model = launcher.buildModel();
		List<CtArrayTypeReference<?>> refs = model.getElements(e -> true);

		int loopIterations = 0; // for meta assert
		for (CtArrayTypeReference<?> arrayTypeRef : refs) {
		    CtTypeReference<?> compType = getDeepestComponentType(arrayTypeRef);
		    assertFalse(compType.getPackage().isImplicit());
			loopIterations++;
		}

		assertTrue("Test loop did not execute", loopIterations > 0);
	}

	private static CtTypeReference<?> getDeepestComponentType(CtArrayTypeReference<?> arrayTypeRef) {
		CtReference ref = arrayTypeRef;
		while (ref instanceof CtArrayTypeReference) {
			ref = ((CtArrayTypeReference<?>) ref).getComponentType();
		}
		return (CtTypeReference<?>) ref;
	}

	@Test
	public void testUnqualifiedExternalTypeMemberAccess() {
		// contract: if a type member is accessed without qualification, but it is not part of the current
		// compilation unit, any qualification attached to it through import resolution must remain implicit
		// See #3363 for details
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/UnqualifiedExternalTypeMemberAccess.java");
		launcher.getEnvironment().setNoClasspath(true);
		CtModel model = launcher.buildModel();
		List<CtTypeReference<?>> typeReferences = model.getElements(e -> e.getSimpleName().equals("SOMETHING"));

		assertEquals("There should only be one reference to SOMETHING, check the resource!", 1, typeReferences.size());

		CtTypeReference<?> typeRef = typeReferences.get(0);
		CtTypeReference<?> declType = typeRef.getDeclaringType();

		assertEquals("Constants", declType.getSimpleName());
		assertTrue(declType.isImplicit());
	}
}
