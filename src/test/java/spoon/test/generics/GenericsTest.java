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
package spoon.test.generics;

import org.junit.Test;
import spoon.ContractVerifier;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.CtModel;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.comparator.CtLineElementComparator;
import spoon.support.reflect.reference.CtTypeParameterReferenceImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;
import spoon.support.util.SortedList;
import spoon.support.visitor.ClassTypingContext;
import spoon.support.visitor.GenericTypeAdapter;
import spoon.support.visitor.MethodTypingContext;
import spoon.test.ctType.testclasses.ErasureModelA;
import spoon.test.generics.testclasses.Banana;
import spoon.test.generics.testclasses.CelebrationLunch;
import spoon.test.generics.testclasses.CelebrationLunch.WeddingLunch;
import spoon.test.generics.testclasses2.LikeCtClass;
import spoon.test.generics.testclasses2.LikeCtClassImpl;
import spoon.test.generics.testclasses2.SameSignature2;
import spoon.test.generics.testclasses2.SameSignature3;
import spoon.test.generics.testclasses3.Bar;
import spoon.test.generics.testclasses3.ClassThatBindsAGenericType;
import spoon.test.generics.testclasses3.ClassThatDefinesANewTypeArgument;
import spoon.test.generics.testclasses3.Foo;
import spoon.test.generics.testclasses3.GenericConstructor;
import spoon.test.generics.testclasses.EnumSetOf;
import spoon.test.generics.testclasses.FakeTpl;
import spoon.test.generics.testclasses.Lunch;
import spoon.test.generics.testclasses.Mole;
import spoon.test.generics.testclasses.Orange;
import spoon.test.generics.testclasses.OuterTypeParameter;
import spoon.test.generics.testclasses.Paella;
import spoon.test.generics.testclasses.Panini;
import spoon.test.generics.testclasses.SameSignature;
import spoon.test.generics.testclasses.Spaghetti;
import spoon.test.generics.testclasses.Tacos;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.buildClass;
import static spoon.testing.utils.ModelUtils.buildNoClasspath;
import static spoon.testing.utils.ModelUtils.canBeBuilt;
import static spoon.testing.utils.ModelUtils.createFactory;

public class GenericsTest {

	@Test
	public void testBugComparableComparator() throws Exception {
		CtClass<?> type = build("spoon.test.generics.testclasses3",
				"ComparableComparatorBug");

		assertEquals("ComparableComparatorBug", type.getSimpleName());

		CtField<?> field = type
				.getElements(
						new TypeFilter<CtField<?>>(CtField.class))
				.get(1);

		assertEquals(0, field.getType().getActualTypeArguments().size());
		assertEquals(0, ((CtConstructorCall<?>) field.getDefaultExpression()).getType().getActualTypeArguments().size());
	}

	@Test
	public void testModelBuildingTree() throws Exception {
		CtClass<?> type = build("spoon.test.generics.testclasses3", "Tree");
		assertEquals("Tree", type.getSimpleName());

		// New type parameter declaration.
		CtTypeParameter typeParameter = type.getFormalCtTypeParameters().get(0);
		assertEquals("V", typeParameter.getSimpleName());
		assertEquals("[java.io.Serializable, java.lang.Comparable<V>]", typeParameter.getSuperclass().asCtIntersectionTypeReference().getBounds().toString());

		CtMethod<?> node5 = type.getElements(
				new NamedElementFilter<>(CtMethod.class,"node5")).get(0);
		assertEquals(
				"this.<java.lang.Class<? extends java.lang.Throwable>>foo()",
				node5.getBody().getStatement(0).toString());
	}

	@Test
	public void testModelBuildingGenericConstructor() throws Exception {
		CtClass<?> type = build("spoon.test.generics.testclasses3", "GenericConstructor");
		assertEquals("GenericConstructor", type.getSimpleName());
		CtTypeParameter typeParameter = type.getElements(new TypeFilter<CtConstructor<?>>(CtConstructor.class)).get(0).getFormalCtTypeParameters().get(0);
		assertEquals("E", typeParameter.getSimpleName());
	}

	@Test
	public void testDiamond2() throws Exception {
		CtClass<GenericConstructor> type = build("spoon.test.generics.testclasses3", "GenericConstructor");
		assertEquals("GenericConstructor", type.getSimpleName());
		CtConstructor<GenericConstructor> c = type.getConstructor();
		CtLocalVariable<?> var = c.getBody().getStatement(1);
		assertEquals("java.lang.Integer", var.getType().getActualTypeArguments().get(0).getQualifiedName());
		CtConstructorCall<?> constructorCall = (CtConstructorCall<?>) var.getDefaultExpression();
		// diamond operator should have empty type arguments???
		assertTrue(constructorCall.getExecutable().getActualTypeArguments().isEmpty());
	}

	@Test
	public void testDiamond1() {
		Factory factory = createFactory();
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"class Diamond {\n"
								+ "	java.util.List<String> f = new java.util.ArrayList<>();\n"
								+ "}").compile();
		CtField<?> f = clazz.getFields().get(0);
		CtConstructorCall<?> val = (CtConstructorCall<?>) f.getDefaultExpression();

		// the diamond is resolved to String but we don't prettyprint it in the diamond.
		assertTrue(val.getType().getActualTypeArguments().get(0).isImplicit());
		assertEquals("java.lang.String", val.getType().getActualTypeArguments().get(0).toString());
		assertEquals("java.lang.String", val.getType().getActualTypeArguments().get(0).getQualifiedName());
		assertEquals("java.util.ArrayList<>", val.getType().toString());
		assertEquals("new ArrayList<>()",val.prettyprint());
	}

	@Test
	public void testModelBuildingSimilarSignatureMethods() throws Exception {
		CtClass<?> type = build("spoon.test.generics.testclasses3", "SimilarSignatureMethodes");
		List<CtNamedElement> methods = type.getElements(new NamedElementFilter<>(CtNamedElement.class,"methode"));
		assertEquals(2, methods.size());
		CtTypeParameter typeParameter = ((CtMethod<?>) methods.get(0)).getFormalCtTypeParameters().get(0);
		assertEquals("E", typeParameter.getSimpleName());
		CtParameter<?> param = ((CtMethod<?>) methods.get(0)).getParameters().get(0);
		assertEquals("E", param.getType().toString());
	}

	@Test
	public void testTypeParameterReference() throws Exception {
		Factory factory = build(ClassThatBindsAGenericType.class, ClassThatDefinesANewTypeArgument.class);
		CtClass<?> classThatBindsAGenericType = factory.Class().get(ClassThatBindsAGenericType.class);
		CtClass<?> classThatDefinesANewTypeArgument = factory.Class().get(ClassThatDefinesANewTypeArgument.class);

		CtTypeReference<?> tr1 = classThatBindsAGenericType.getSuperclass();
		CtTypeReference<?> trExtends = tr1.getActualTypeArguments().get(0);
		CtTypeParameter tr2 = classThatDefinesANewTypeArgument.getFormalCtTypeParameters().get(0);
		CtTypeReference<?> tr3 = classThatDefinesANewTypeArgument.getMethodsByName("foo").get(0).getParameters().get(0).getReference().getType();

		// an bound type is not an TypeParameterRefernce
		assertTrue(!(trExtends instanceof CtTypeParameterReference));

		// a used type parameter T is a CtTypeParameterReference
		assertTrue(tr3 instanceof CtTypeParameterReference);

		assertEquals("File", trExtends.getSimpleName());
		assertSame(java.io.File.class, trExtends.getActualClass());
		assertEquals("T", tr2.getSimpleName());
		assertEquals("T", tr3.getSimpleName());
	}

	@Test
	public void testTypeParameterDeclarer() throws Exception {
		// contract: one can lookup the declarer of a type parameter if it is in appropriate context (the declararer is in the parent hierarchy)
		CtClass<?> classThatDefinesANewTypeArgument = build("spoon.test.generics.testclasses3", "ClassThatDefinesANewTypeArgument");
		CtTypeParameter typeParam = classThatDefinesANewTypeArgument.getFormalCtTypeParameters().get(0);
		assertEquals("T", classThatDefinesANewTypeArgument.getFormalCtTypeParameters().get(0).getSimpleName());
		assertSame(classThatDefinesANewTypeArgument, typeParam.getTypeParameterDeclarer());
		CtTypeParameterReference typeParamReference = typeParam.getReference();
		assertSame(typeParam, typeParamReference.getDeclaration());

		// creating an appropriate context
		CtMethod m = classThatDefinesANewTypeArgument.getFactory().createMethod();
		m.setParent(classThatDefinesANewTypeArgument);
		// setting the return type of the method
		m.setType(typeParamReference);
		classThatDefinesANewTypeArgument.addMethod(m);

		// the final assertions
		assertSame(typeParam, typeParamReference.getDeclaration());

		assertSame(classThatDefinesANewTypeArgument, typeParamReference.getDeclaration().getParent());

		// now testing that the getDeclaration of a type parameter is actually a dynamic lookup
		CtClass<?> c2 = classThatDefinesANewTypeArgument.clone();
		c2.addMethod(m);
		assertSame(c2, typeParamReference.getDeclaration().getParent());
		// even if we rename it
		typeParamReference.setSimpleName("R"); // renaming the reference
		c2.getFormalCtTypeParameters().get(0).setSimpleName("R"); // renaming the declaration
		assertSame(c2, typeParamReference.getDeclaration().getParent());
	}

	@Test
	public void testGenericMethodCallWithExtend() throws Exception {
		CtClass<?> type = build("spoon.test.generics.testclasses3", "GenericMethodCallWithExtend");
		CtMethod<?> meth = type.getMethodsByName("methode").get(0);

		// an bound type is not an TypeParameterRefernce
		assertEquals("E extends java.lang.Enum<E>", meth.getFormalCtTypeParameters().get(0).toString());

		meth = type.getMethod("m2");
		assertEquals("A extends java.lang.Number & java.lang.Comparable<? super A>", meth.getFormalCtTypeParameters().get(0).toString());
	}

	@Test
	public void testBugCommonCollection() throws Exception {
		try {
			CtClass<?> type = build("spoon.test.generics.testclasses3", "BugCollection");

			CtField<?> INSTANCE = type.getElements(
					new NamedElementFilter<>(CtField.class,"INSTANCE")).get(0);
			// assertTrue(INSTANCE.getDefaultExpression().getType().getActualTypeArguments().get(0)
			// instanceof CtAnnonTypeParameterReference);
			assertEquals(
					"public static final spoon.test.generics.testclasses3.ACLass<?> INSTANCE = new spoon.test.generics.testclasses3.ACLass();",
					INSTANCE.toString());

			CtField<?> INSTANCE2 = type.getElements(
					new NamedElementFilter<>(CtField.class,"INSTANCE2")).get(0);
			INSTANCE2.setAnnotations(new ArrayList<>());
			assertEquals(
					"public static final spoon.test.generics.testclasses3.ACLass<?> INSTANCE2 = new spoon.test.generics.testclasses3.ACLass();",
					INSTANCE2.toString());

			CtClass<?> ComparableComparator = type
					.getPackage()
					.getElements(
							new NamedElementFilter<>(CtClass.class,"ComparableComparator"))
					.get(0);
			assertTrue(ComparableComparator
					.toString()
					.startsWith(
							"class ComparableComparator<E extends java.lang.Comparable<? super E>>"));

			CtField<?> x = type.getElements(new NamedElementFilter<>(CtField.class,"x"))
					.get(0);
			CtTypeReference<?> ref = x.getType();

			// qualifed name
			assertEquals("java.util.Map$Entry", ref.getQualifiedName());

			// toString uses DefaultJavaPrettyPrinter with forced fully qualified names
			assertEquals("java.util.Map.Entry", ref.toString());

			assertSame(java.util.Map.class, ref.getDeclaringType()
					.getActualClass());

			CtField<?> y = type.getElements(new NamedElementFilter<>(CtField.class,"y"))
					.get(0);
			assertEquals("java.util.Map.Entry<?, ?> y;", y.toString());

			CtField<?> z = type.getElements(new NamedElementFilter<>(CtField.class,"z"))
					.get(0);
			assertEquals(
					"java.util.Map.Entry<java.lang.String, java.lang.Integer> z;",
					z.toString());

			// now as local variables
			CtLocalVariable<?> lx = type.getElements(
					new NamedElementFilter<>(CtLocalVariable.class,"lx")).get(0);
			assertEquals("java.util.Map.Entry lx", lx.toString());

			CtLocalVariable<?> ly = type.getElements(
					new NamedElementFilter<>(CtLocalVariable.class,"ly")).get(0);
			assertEquals("java.util.Map.Entry<?, ?> ly", ly.toString());

			CtLocalVariable<?> lz = type.getElements(
					new NamedElementFilter<>(CtLocalVariable.class,"lz")).get(0);
			assertEquals(
					"java.util.Map.Entry<java.lang.String, java.lang.Integer> lz",
					lz.toString());

			CtLocalVariable<?> it = type.getElements(
					new NamedElementFilter<>(CtLocalVariable.class,"it")).get(0);
			assertEquals("java.util.Iterator<java.util.Map.Entry<?, ?>> it",
					it.toString());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void testInstanceOfMapEntryGeneric() throws Exception {
		CtClass<?> type = build("spoon.test.generics.testclasses3", "InstanceOfMapEntryGeneric");
		CtMethod<?> meth = type.getMethodsByName("methode").get(0);

		CtBinaryOperator<?> instOf = (CtBinaryOperator<?>) ((CtLocalVariable<?>) meth.getBody().getStatement(0)).getDefaultExpression();
		assertEquals(BinaryOperatorKind.INSTANCEOF, instOf.getKind());
		assertEquals("o instanceof java.util.Map.Entry<?, ?>", instOf.toString());
	}

	@Test
	public void testAccessToGenerics() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();

		SpoonModelBuilder compiler = spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources(
						"./src/test/java/spoon/test/generics/testclasses3/Foo.java",
						"./src/test/java/spoon/test/generics/testclasses3/Bar.java"));

		compiler.build();

		CtClass<?> foo = (CtClass<?>) factory.Type().get(Foo.class);
		CtInterface<?> bar = (CtInterface<?>) factory.Type().get(Bar.class);
		final CtNewClass<?> newAnonymousBar = foo.getElements(new AbstractFilter<CtNewClass<?>>(CtNewClass.class) {
			@Override
			public boolean matches(CtNewClass<?> element) {
				return element.getAnonymousClass() != null && element.getAnonymousClass().isAnonymous();
			}
		}).get(0);

		final List<CtTypeParameter> barTypeParamGenerics = bar.getFormalCtTypeParameters();
		final CtTypeReference<?> anonymousBar = newAnonymousBar.getType();

		assertEquals("Name of the first generic parameter in Bar interface must to be I.", "I", barTypeParamGenerics.get(0).getSimpleName());
		assertEquals("Name of the first generic parameter in Bar usage must to be K.", "K", anonymousBar.getActualTypeArguments().get(0).getSimpleName());

		assertEquals("Name of the second generic parameter in Bar interface must to be O.", "O", barTypeParamGenerics.get(1).getSimpleName());
		assertEquals("Name of the second generic parameter in Bar usage must to be V.", "V", anonymousBar.getActualTypeArguments().get(1).getSimpleName());
	}

	@Test
	public void testConstructorCallGenerics() {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/generics/testclasses/",
				"-o", "./target/spooned/"
		});

		final CtClass<?> aTacos = launcher.getFactory().Class().get(Tacos.class);
		assertEquals(2, aTacos.getFormalCtTypeParameters().size());
		final CtTypeReference interfaces = aTacos.getSuperInterfaces().toArray(new CtTypeReference[0])[0];
		assertEquals(1, interfaces.getActualTypeArguments().size());

		final CtMethod<?> m = aTacos.getMethodsByName("m").get(0);
		final CtElement local1 = m.getBody().getStatement(0).getElements(new TypeFilter<>(CtLocalVariable.class)).get(0);
		final CtTypeReference<Object> leftSideLocal1 = (CtTypeReference<Object>) local1.getElements(new ReferenceTypeFilter<>(CtTypeReference.class)).get(0);
		final CtConstructorCall<Object> rightSideLocal1 = (CtConstructorCall<Object>) local1.getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);
		assertEquals(1, leftSideLocal1.getActualTypeArguments().size());
		assertEquals(1, rightSideLocal1.getType().getActualTypeArguments().size());
		assertEquals("java.util.List<java.lang.String> l = new java.util.ArrayList<>()", local1.toString());

		final CtElement local2 = m.getBody().getStatement(1).getElements(new TypeFilter<>(CtLocalVariable.class)).get(0);
		final CtTypeReference<Object> leftSideLocal2 = (CtTypeReference<Object>) local2.getElements(new ReferenceTypeFilter<>(CtTypeReference.class)).get(0);
		assertEquals(0, leftSideLocal2.getActualTypeArguments().size());
		assertEquals("java.util.List l2", local2.toString());

		final CtElement local3 = m.getBody().getStatement(2).getElements(new TypeFilter<>(CtLocalVariable.class)).get(0);
		final CtTypeReference<Object> leftSideLocal3 = (CtTypeReference<Object>) local3.getElements(new ReferenceTypeFilter<>(CtTypeReference.class)).get(0);
		final CtConstructorCall<Object> rightSideLocal3 = (CtConstructorCall<Object>) local3.getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);
		assertEquals(2, leftSideLocal3.getActualTypeArguments().size());
		assertEquals(2, rightSideLocal3.getType().getActualTypeArguments().size());
		assertEquals("spoon.test.generics.testclasses.IBurritos<?, ?> burritos = new Burritos<>()", local3.toString());

		final CtElement local4 = m.getBody().getStatement(3).getElements(new TypeFilter<>(CtLocalVariable.class)).get(0);
		final CtTypeReference<Object> leftSideLocal4 = (CtTypeReference<Object>) local4.getElements(new ReferenceTypeFilter<>(CtTypeReference.class)).get(0);
		final CtConstructorCall<Object> rightSideLocal4 = (CtConstructorCall<Object>) local4.getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);
		assertEquals(1, leftSideLocal4.getActualTypeArguments().size());
		assertEquals(1, rightSideLocal4.getType().getActualTypeArguments().size());
		assertEquals("java.util.List<?> l3 = new java.util.ArrayList<java.lang.Object>()", local4.toString());

		final CtConstructorCall constructorCall1 = (CtConstructorCall) m.getBody().getStatement(4).getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);
		assertEquals(1, constructorCall1.getActualTypeArguments().size());
		assertEquals(2, constructorCall1.getType().getActualTypeArguments().size());
		assertEquals("new <java.lang.Integer>spoon.test.generics.testclasses.Tacos<java.lang.Object, java.lang.String>()", constructorCall1.toString());

		final CtConstructorCall constructorCall2 = (CtConstructorCall) m.getBody().getStatement(5).getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);
		assertEquals(0, constructorCall2.getActualTypeArguments().size());
		assertEquals(2, constructorCall2.getType().getActualTypeArguments().size());
		assertEquals("new spoon.test.generics.testclasses.Tacos<>()", constructorCall2.toString());

		canBeBuilt("./target/spooned/spoon/test/generics/testclasses/", 8);
	}

	@Test
	public void testInvocationGenerics() {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/generics/testclasses/",
				"-o", "./target/spooned/"
		});

		final CtClass<?> aTacos = launcher.getFactory().Class().get(Tacos.class);

		final CtConstructor<?> defaultConstructor = aTacos.getConstructor();
		final CtInvocation<?> explicitConstructorCall = (CtInvocation<?>) defaultConstructor.getBody().getStatement(0).getElements(new TypeFilter<>(CtInvocation.class)).get(0);
		assertEquals(1, explicitConstructorCall.getExecutable().getActualTypeArguments().size());
		assertEquals("<java.lang.String>this(1)", explicitConstructorCall.toString());

		final CtMethod<?> m = aTacos.getMethodsByName("m2").get(0);
		final CtInvocation invocation1 = m.getBody().getStatement(0).getElements(new TypeFilter<>(CtInvocation.class)).get(0);
		assertEquals(1, invocation1.getExecutable().getActualTypeArguments().size());
		assertEquals("this.<java.lang.String>makeTacos(null)", invocation1.toString());

		final CtInvocation invocation2 = m.getBody().getStatement(1).getElements(new TypeFilter<>(CtInvocation.class)).get(0);
		assertEquals(0, invocation2.getExecutable().getActualTypeArguments().size());
		assertEquals("this.makeTacos(null)", invocation2.toString());

		canBeBuilt("./target/spooned/spoon/test/generics/testclasses/", 8);
	}

	@Test
	public void testNewClassGenerics() {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/generics/testclasses/",
				"-o", "./target/spooned/"
		});

		final CtClass<?> aTacos = launcher.getFactory().Class().get(Tacos.class);

		final CtMethod<?> m = aTacos.getMethodsByName("m3").get(0);
		final CtNewClass newClass1 = m.getBody().getStatement(0).getElements(new TypeFilter<>(CtNewClass.class)).get(0);
		assertEquals(0, newClass1.getActualTypeArguments().size());
		assertEquals(2, newClass1.getType().getActualTypeArguments().size());
		assertEquals("new javax.lang.model.util.SimpleTypeVisitor7<spoon.test.generics.testclasses.Tacos, java.lang.Void>() {}", newClass1.toString());

		final CtNewClass newClass2 = m.getBody().getStatement(1).getElements(new TypeFilter<>(CtNewClass.class)).get(0);
		assertEquals(0, newClass2.getActualTypeArguments().size());
		assertEquals(2, newClass2.getType().getActualTypeArguments().size());
		assertEquals("new javax.lang.model.util.SimpleTypeVisitor7<spoon.test.generics.testclasses.Tacos, java.lang.Void>() {}", newClass2.toString());

		canBeBuilt("./target/spooned/spoon/test/generics/testclasses/", 8);
	}

	@Test
	public void testMethodsWithGenericsWhoExtendsObject() {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/generics/testclasses/",
				"-o", "./target/spooned/"
		});

		final CtClass<?> aTacos = launcher.getFactory().Class().get(Tacos.class);

		final CtMethod<?> m = aTacos.getMethodsByName("m4").get(0);
		final CtInvocation<?> invocation1 = m.getBody().getStatement(0).getElements(new TypeFilter<>(CtInvocation.class)).get(0);
		assertEquals(2, invocation1.getExecutable().getActualTypeArguments().size());
		assertEquals("spoon.test.generics.testclasses.Tacos.<V, C>makeTacos()", invocation1.toString());

		final CtInvocation<?> invocation2 = m.getBody().getStatement(1).getElements(new TypeFilter<>(CtInvocation.class)).get(0);
		assertEquals(0, invocation2.getExecutable().getActualTypeArguments().size());
		assertEquals("spoon.test.generics.testclasses.Tacos.makeTacos()", invocation2.toString());
	}

	@Test
	public void testName() {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/generics/testclasses/",
				"-o", "./target/spooned/"
		});

		final CtClass<?> aFactory = launcher.getFactory().Class().get(Tacos.BeerFactory.class);

		final CtMethod<?> m = aFactory.getMethodsByName("newBeer").get(0);
		final CtConstructorCall constructorCall1 = m.getBody().getStatement(0).getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);
		assertEquals("new Beer()", constructorCall1.toString());
	}

	@Test
	public void testGenericWithExtendsInDeclaration() throws Exception {
		final Factory build = build(Panini.class);
		final CtType<Panini> panini = build.Type().get(Panini.class);

		final CtMethod<?> apply = panini.getMethodsByName("apply").get(0);
		assertEquals(1, apply.getType().getActualTypeArguments().size());
		assertEquals("? super java.lang.Object", apply.getType().getActualTypeArguments().get(0).toString());

		assertEquals(1, apply.getParameters().get(0).getType().getActualTypeArguments().size());
		assertEquals("? extends java.lang.Long", apply.getParameters().get(0).getType().getActualTypeArguments().get(0).toString());
	}

	@Test
	public void testGenericInField() throws Exception {
		final Factory build = build(Spaghetti.class);
		final CtType<Panini> aSpaghetti = build.Type().get(Spaghetti.class);

		assertTrue(aSpaghetti.toString().contains("private spoon.test.generics.testclasses.Spaghetti<B>.Tester tester;"));
		assertTrue(aSpaghetti.toString().contains("private spoon.test.generics.testclasses.Spaghetti<B>.Tester tester1;"));

		assertTrue(aSpaghetti.toString().contains("private spoon.test.generics.testclasses.Spaghetti<B>.That<java.lang.String, java.lang.String> field;"));
		assertTrue(aSpaghetti.toString().contains("private spoon.test.generics.testclasses.Spaghetti<java.lang.String>.That<java.lang.String, java.lang.String> field1;"));
		assertTrue(aSpaghetti.toString().contains("private spoon.test.generics.testclasses.Spaghetti<java.lang.Number>.That<java.lang.String, java.lang.String> field2;"));
	}

	@Test
	public void testGenericsInQualifiedNameInConstructorCall() {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/generics/testclasses/",
				"-o", "./target/spooned/"
		});

		final CtClass<Tacos> aTacos = launcher.getFactory().Class().get(Tacos.class);
		final CtType<?> burritos = aTacos.getNestedType("Burritos");

		SortedList<CtConstructorCall> elements = new SortedList<>(new CtLineElementComparator());
		elements.addAll(burritos.getElements(new TypeFilter<>(CtConstructorCall.class)));

		assertEquals(3, elements.size());

		// Constructor call.
		assertEquals(0, elements.get(1).getExecutable().getType().getActualTypeArguments().size());
		assertNotNull(elements.get(1).getType().getDeclaringType());
		assertEquals("new Pozole()", elements.get(1).toString());
		assertEquals(2, elements.get(0).getExecutable().getType().getActualTypeArguments().size());
		assertNotNull(elements.get(0).getType().getDeclaringType());
		assertEquals("new Burritos<K, V>()", elements.get(0).toString());

		// New class.
		assertEquals(2, elements.get(2).getExecutable().getType().getActualTypeArguments().size());
		assertNotNull(elements.get(2).getType().getDeclaringType());
		assertEquals("new Burritos<K, V>() {}", elements.get(2).toString());
	}

	@Test
	public void testGenericsOnLocalType() throws Exception {
		// contract: A local type can have actual generic types.
		final CtType<Mole> aMole = buildClass(Mole.class);
		final CtMethod<Object> cook = aMole.getMethod("cook");
		final CtConstructorCall<?> newCook = cook.getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);

		assertEquals(1, newCook.getType().getActualTypeArguments().size());
		assertEquals("new Cook<java.lang.String>()", newCook.toString());
	}

	@Test
	public void testGenericsInConstructorCall() throws Exception {
		// contract: A constructor call have generics declared before the type and the
		// TypeReference have generics declared after itself. e.g, new <String>Test<String>().
		final CtType<Mole> aMole = buildClass(Mole.class);
		final CtMethod<Object> prepare = aMole.getMethod("prepare");
		final CtConstructorCall<?> newPrepare = prepare.getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);

		assertEquals(1, newPrepare.getActualTypeArguments().size());
		assertEquals("java.lang.Integer", newPrepare.getActualTypeArguments().get(0).toString());
		assertEquals(1, newPrepare.getType().getActualTypeArguments().size());
		assertEquals("java.lang.String", newPrepare.getType().getActualTypeArguments().get(0).toString());
	}

	@Test
	public void testWildcard() throws Exception {
		List<CtWildcardReference> wildcardReferences = buildClass(Paella.class).getElements(new TypeFilter<>(CtWildcardReference.class));
		// 3 = the class declaration + the constructor declaration + the method declaration
		assertEquals(3, wildcardReferences.size());
	}

	@Test
	public void testGetDeclarationOnGenericReturnType() throws Exception {
		//contract: generic return type reference can access parameter type.
		CtMethod<?> method = buildClass(Paella.class).getMethodsByName("make").get(0);
		CtTypeParameterReference paramTypeRef = (CtTypeParameterReference) method.getType();
		assertEquals("T", paramTypeRef.getSimpleName());
		assertSame(method.getFormalCtTypeParameters().get(0), paramTypeRef.getTypeParameterDeclaration());
	}

	@Test
	public void testDeclarationOfTypeParameterReference() throws Exception {
		CtType<Tacos> aTacos = buildNoClasspath(Tacos.class).Type().get(Tacos.class);
		for (CtTypeParameterReference parameterReference : aTacos.getElements(new TypeFilter<CtTypeParameterReference>(CtTypeParameterReference.class) {
			@Override
			public boolean matches(CtTypeParameterReference element) {
				return !(element instanceof CtWildcardReference) && !(element.getParent() instanceof CtReference);
			}
		})) {
			assertNotNull(parameterReference.getDeclaration());
		}
	}

	@Test
	public void testIsGenericsMethod() throws Exception {
		CtType<Tacos> aTacos = buildNoClasspath(Tacos.class).Type().get(Tacos.class);
		CtTypeParameter typeParameter = aTacos.getFormalCtTypeParameters().get(0);
		assertTrue(typeParameter.isGenerics());
		assertTrue(typeParameter.getReference().isGenerics());

		CtTypeReference ctTypeReference = aTacos.getSuperInterfaces().toArray(new CtTypeReference[aTacos.getSuperInterfaces().size()])[0];
		assertTrue(aTacos.isGenerics());

		// this is a generic type reference spoon.test.generics.testclasses.ITacos<V>
		assertEquals("spoon.test.generics.testclasses.ITacos<V>", ctTypeReference.toString());
		assertTrue(ctTypeReference.isGenerics());
	}
	@Test
	public void testTypeParameterReferenceAsActualTypeArgument() throws Exception {
		CtType<Tacos> aTacos = buildNoClasspath(ClassThatDefinesANewTypeArgument.class).Type().get(ClassThatDefinesANewTypeArgument.class);
		
		CtTypeReference<?> typeRef = aTacos.getReference();

		assertSame(aTacos, typeRef.getDeclaration());

		CtTypeParameter typeParam = aTacos.getFormalCtTypeParameters().get(0);
		CtTypeParameterReference typeParamRef = typeParam.getReference();
		assertSame(typeParam, typeParamRef.getDeclaration());

		assertEquals("spoon.test.generics.testclasses3.ClassThatDefinesANewTypeArgument", typeRef.toString());

		// creating a reference to "ClassThatDefinesANewTypeArgument<T>"
		//this assignment changes parent of typeParamRef to TYPEREF
		typeRef.addActualTypeArgument(typeParamRef);

		assertEquals("spoon.test.generics.testclasses3.ClassThatDefinesANewTypeArgument<T>", typeRef.toString());

		// this does not change the declaration
		assertSame(aTacos, typeRef.getDeclaration());
		//stored typeParamRef is same like the added one, no clone - OK
		assertSame(typeParamRef, typeRef.getActualTypeArguments().get(0));
		//typeParamRef has got new parent 
		assertSame(typeRef, typeParamRef.getParent());

		assertEquals(typeParam, typeParamRef.getDeclaration());
		assertEquals(typeParam, typeParamRef.getTypeParameterDeclaration());
		typeParamRef.setSimpleName("Y");
		assertEquals(typeParam, typeParamRef.getTypeParameterDeclaration());
	}
	@Test
	public void testGenericTypeReference() throws Exception {

		// contract: the parameter includingFormalTypeParameter of createReference enables one to also create actual type arguments

		CtType<Tacos> aTacos = buildNoClasspath(Tacos.class).Type().get(Tacos.class);
		//this returns a type reference with uninitialized actual type arguments.
		CtTypeReference<?> genericTypeRef = aTacos.getFactory().Type().createReference(aTacos, true);

		assertFalse(genericTypeRef.getActualTypeArguments().isEmpty());
		assertEquals(aTacos.getFormalCtTypeParameters().size(), genericTypeRef.getActualTypeArguments().size());
		for(int i=0; i<aTacos.getFormalCtTypeParameters().size(); i++) {
			assertSame("TypeParameter reference idx="+i+" is different", aTacos.getFormalCtTypeParameters().get(i), genericTypeRef.getActualTypeArguments().get(i).getTypeParameterDeclaration());

			// contract: getTypeParameterDeclaration goes back to the declaration, even without context
			assertSame(aTacos.getFormalCtTypeParameters().get(i), genericTypeRef.getActualTypeArguments().get(i).getTypeParameterDeclaration());

		}
	}
	@Test
	public void testisGeneric() {
		Factory factory = build(new File("src/test/java/spoon/test/generics/testclasses"));

		/*
		// this code has been used to generate the list of assertEquals below,
		// and then each assertEquals was verified

		Set<String> s = new HashSet<>();
		factory.getModel().getElements(new TypeFilter<CtTypeReference>(CtTypeReference.class) {
			@Override
			public boolean matches(CtTypeReference element) {
				return super.matches(element) && element.getParent() instanceof CtVariable;
			}
		}).forEach(x -> {
				String simpleName = ((CtVariable) x.getParent()).getSimpleName();
				if (!s.contains(simpleName)) {
					System.out.println("\t\t// "+x.toString());
					System.out.println("\t\tCtTypeReference<?> "+simpleName+"Ref = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, \"" + simpleName+ "\")).first(CtVariable.class).getType();");
					System.out.println("\t\tassertEquals("+x.isGeneric() + ", " + simpleName + "Ref.isGeneric());");
					System.out.println();
				}
				s.add(simpleName);
		});
		 */

		// T
		CtTypeReference<?> var1Ref = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "var1")).first(CtVariable.class).getType();
		assertTrue(var1Ref.isGenerics());

		// spoon.test.generics.testclasses.rxjava.Subscriber<? super T>
		CtTypeReference<?> sRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "s")).first(CtVariable.class).getType();
		assertTrue(sRef.isGenerics());

		// spoon.test.generics.testclasses.rxjava.Try<java.util.Optional<java.lang.Object>>
		CtTypeReference<?> notificationRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "notification")).first(CtVariable.class).getType();
		assertFalse(notificationRef.isGenerics());

		// java.util.function.Function<? super spoon.test.generics.testclasses.rxjava.Observable<spoon.test.generics.testclasses.rxjava.Try<java.util.Optional<java.lang.Object>>>, ? extends spoon.test.generics.testclasses.rxjava.Publisher<?>>
		CtTypeReference<?> managerRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "manager")).first(CtVariable.class).getType();
		assertFalse(managerRef.isGenerics());

		// spoon.test.generics.testclasses.rxjava.BehaviorSubject<spoon.test.generics.testclasses.rxjava.Try<java.util.Optional<java.lang.Object>>>
		CtTypeReference<?> subjectRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "subject")).first(CtVariable.class).getType();
		assertFalse(subjectRef.isGenerics());

		// spoon.test.generics.testclasses.rxjava.PublisherRedo.RedoSubscriber<T>
		CtTypeReference<?> parentRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "parent")).first(CtVariable.class).getType();
		assertTrue(parentRef.isGenerics());

		// spoon.test.generics.testclasses.rxjava.Publisher<?>
		CtTypeReference<?> actionRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "action")).first(CtVariable.class).getType();
		assertFalse(actionRef.isGenerics());

		// spoon.test.generics.testclasses.rxjava.ToNotificationSubscriber
		CtTypeReference<?> trucRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "truc")).first(CtVariable.class).getType();
		assertFalse(trucRef.isGenerics());

		// java.util.function.Consumer<? super spoon.test.generics.testclasses.rxjava.Try<java.util.Optional<java.lang.Object>>>
		CtTypeReference<?> consumerRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "consumer")).first(CtVariable.class).getType();
		assertFalse(consumerRef.isGenerics());

		// S
		CtTypeReference<?> sectionRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "section")).first(CtVariable.class).getType();
		assertTrue(sectionRef.isGenerics());

		// X
		CtTypeReference<?> paramARef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "paramA")).first(CtVariable.class).getType();
		assertTrue(paramARef.isGenerics());

		// spoon.test.generics.testclasses.Tacos
		CtTypeReference<?> paramBRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "paramB")).first(CtVariable.class).getType();
		assertFalse(paramBRef.isGenerics());

		// C
		CtTypeReference<?> paramCRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "paramC")).first(CtVariable.class).getType();
		assertTrue(paramCRef.isGenerics());

		// R
		CtTypeReference<?> cookRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "cook")).first(CtVariable.class).getType();
		assertTrue(cookRef.isGenerics());

		// spoon.test.generics.testclasses.CelebrationLunch<java.lang.Integer, java.lang.Long, java.lang.Double>
		CtTypeReference<?> clRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "cl")).first(CtVariable.class).getType();
		assertFalse(clRef.isGenerics());

		// spoon.test.generics.testclasses.CelebrationLunch<java.lang.Integer, java.lang.Long, java.lang.Double>.WeddingLunch<spoon.test.generics.testclasses.Mole>
		CtTypeReference<?> disgustRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "disgust")).first(CtVariable.class).getType();
		assertFalse(disgustRef.isGenerics());

		// L
		CtTypeReference<?> paramRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "param")).first(CtVariable.class).getType();
		assertTrue(paramRef.isGenerics());

		// spoon.reflect.declaration.CtType<? extends spoon.reflect.declaration.CtNamedElement>
		CtTypeReference<?> targetTypeRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "targetType")).first(CtVariable.class).getType();
		assertFalse(targetTypeRef.isGenerics());

		// spoon.reflect.declaration.CtType<?>
		CtTypeReference<?> somethingRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "something")).first(CtVariable.class).getType();
		assertFalse(somethingRef.isGenerics());

		// int
		CtTypeReference<?> iRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "i")).first(CtVariable.class).getType();
		assertFalse(iRef.isGenerics());

		// T
		CtTypeReference<?> biduleRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "bidule")).first(CtVariable.class).getType();
		assertTrue(biduleRef.isGenerics());

		// Cook<java.lang.String>
		CtTypeReference<?> aClassRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "aClass")).first(CtVariable.class).getType();
		assertFalse(aClassRef.isGenerics());

		// java.util.List<java.util.List<M>>
		CtTypeReference<?> list2mRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "list2m")).first(CtVariable.class).getType();
		assertTrue(list2mRef.isGenerics());

		// spoon.test.generics.testclasses.Panini.Subscriber<? extends java.lang.Long>
		CtTypeReference<?> tRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "t")).first(CtVariable.class).getType();
		assertFalse(tRef.isGenerics());

		// spoon.test.generics.testclasses.Spaghetti<B>.Tester
		CtTypeReference<?> testerRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "tester")).first(CtVariable.class).getType();
		assertFalse(testerRef.isGenerics());

		// spoon.test.generics.testclasses.Spaghetti<B>.Tester
		CtTypeReference<?> tester1Ref = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "tester1")).first(CtVariable.class).getType();
		assertFalse(tester1Ref.isGenerics());

		// spoon.test.generics.testclasses.Spaghetti<B>.That<java.lang.String, java.lang.String>
		CtTypeReference<?> fieldRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "field")).first(CtVariable.class).getType();
		assertFalse(fieldRef.isGenerics());

		// spoon.test.generics.testclasses.Spaghetti<java.lang.String>.That<java.lang.String, java.lang.String>
		CtTypeReference<?> field1Ref = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "field1")).first(CtVariable.class).getType();
		assertFalse(field1Ref.isGenerics());

		// spoon.test.generics.testclasses.Spaghetti<java.lang.Number>.That<java.lang.String, java.lang.String>
		CtTypeReference<?> field2Ref = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "field2")).first(CtVariable.class).getType();
		assertFalse(field2Ref.isGenerics());

		// spoon.test.generics.testclasses.Tacos<K, java.lang.String>.Burritos<K, V>
		CtTypeReference<?> burritosRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "burritos")).first(CtVariable.class).getType();
		// now that the order of type members is correct
		// this burritos is indeed "IBurritos<?, ?> burritos = new Burritos<>()" with no generics
		assertFalse(burritosRef.isGenerics());

		// int
		CtTypeReference<?> nbTacosRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "nbTacos")).first(CtVariable.class).getType();
		assertFalse(nbTacosRef.isGenerics());

		// java.util.List<java.lang.String>
		CtTypeReference<?> lRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "l")).first(CtVariable.class).getType();
		assertFalse(lRef.isGenerics());

		// java.util.List
		CtTypeReference<?> l2Ref = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "l2")).first(CtVariable.class).getType();
		assertFalse(l2Ref.isGenerics());

		// java.util.List<?>
		CtTypeReference<?> l3Ref = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "l3")).first(CtVariable.class).getType();
		assertFalse(l3Ref.isGenerics());

		// T
		CtTypeReference<?> anObjectRef = factory.getModel().filterChildren(new NamedElementFilter(CtVariable.class, "anObject")).first(CtVariable.class).getType();
		assertTrue(anObjectRef.isGenerics());

	}
	@Test
	public void testCtTypeReference_getSuperclass() {
		Factory factory = build(new File("src/test/java/spoon/test/generics/testclasses"));
		CtClass<?> ctClassCelebrationLunch = factory.Class().get(CelebrationLunch.class);
		CtTypeReference<?> trWeddingLunch_Mole = ctClassCelebrationLunch.filterChildren(new NamedElementFilter<>(CtNamedElement.class,"disgust")).map((CtTypedElement te)->{
			return te.getType();
		}).first();
		
		assertEquals("spoon.test.generics.testclasses.CelebrationLunch<java.lang.Integer, java.lang.Long, java.lang.Double>.WeddingLunch<spoon.test.generics.testclasses.Mole>",trWeddingLunch_Mole.toString());
		CtType<?> tWeddingLunch_X = trWeddingLunch_Mole.getDeclaration();
		CtTypeReference<?> trCelebrationLunch_Tacos_Paella_X = tWeddingLunch_X.getSuperclass();
		//current correct behavior of CtType#getSuperclass()
		assertEquals("spoon.test.generics.testclasses.CelebrationLunch<"
				+ "spoon.test.generics.testclasses.Tacos, "
				+ "spoon.test.generics.testclasses.Paella, "
				+ "X"
				+ ">",trCelebrationLunch_Tacos_Paella_X.toString());
		//current - wrong behavior of CtTypeReference#getSuperclass()
		assertEquals("spoon.test.generics.testclasses.CelebrationLunch<"
				+ "spoon.test.generics.testclasses.Tacos, "
				+ "spoon.test.generics.testclasses.Paella, "
				+ "X"
				+ ">",trWeddingLunch_Mole.getSuperclass().toString());
		//future suggested behavior of CtTypeReference#getSuperclass() - the 3rd argument is Mole.
//		assertEquals("spoon.test.generics.testclasses.CelebrationLunch<"
//				+ "spoon.test.generics.testclasses.Tacos, "
//				+ "spoon.test.generics.testclasses.Paella, "
//				+ "spoon.test.generics.testclasses.Mole"
//				+ ">",trWeddingLunch_Mole.getSuperclass().toString());
		//future suggested behavior of CtTypeReference#getSuperclass() 
//		assertEquals("spoon.test.generics.testclasses.Lunch<"
//				+ "spoon.test.generics.testclasses.Mole, "
//				+ "spoon.test.generics.testclasses.Tacos"
//				+ ">",trWeddingLunch_Mole.getSuperclass().getSuperclass().toString());
	}

	@Test
	public void testTypeAdapted() throws Exception {
		// contract: one can get the actual value of a generic type in a given context
		CtClass<?> ctModel = (CtClass<?>) buildClass(ErasureModelA.class);
		CtTypeParameter tpA = ctModel.getFormalCtTypeParameters().get(0);
		CtTypeParameter tpB = ctModel.getFormalCtTypeParameters().get(1);
		CtTypeParameter tpC = ctModel.getFormalCtTypeParameters().get(2);
		CtTypeParameter tpD = ctModel.getFormalCtTypeParameters().get(3);

		CtClass<?> ctModelB = ctModel.filterChildren(new NamedElementFilter<>(CtClass.class,"ModelB")).first();
		ClassTypingContext sth = new ClassTypingContext(ctModelB);
		// in ModelB, "A" is "A2"
		assertEquals("A2", sth.adaptType(tpA).getQualifiedName());
		// in ModelB, "B" is "B2"
		assertEquals("B2", sth.adaptType(tpB).getQualifiedName());
		// and so on and so forth
		assertEquals("C2", sth.adaptType(tpC).getQualifiedName());
		assertEquals("D2", sth.adaptType(tpD).getQualifiedName());

		CtClass<?> ctModelC = ctModel.filterChildren(new NamedElementFilter<>(CtClass.class,"ModelC")).first();
		ClassTypingContext sthC = new ClassTypingContext(ctModelC);
		assertEquals("java.lang.Integer", sthC.adaptType(tpA).getQualifiedName());
		assertEquals("java.lang.RuntimeException", sthC.adaptType(tpB).getQualifiedName());
		assertEquals("java.lang.IllegalArgumentException", sthC.adaptType(tpC).getQualifiedName());
		assertEquals("java.util.List", sthC.adaptType(tpD).getQualifiedName());
	}


	@Test
	public void testClassTypingContext() {
		// contract: a ClassTypingContext enables one to perform type resolution of generic types
		Factory factory = build(new File("src/test/java/spoon/test/generics/testclasses"));
		CtClass<?> ctClassCelebrationLunch = factory.Class().get(CelebrationLunch.class);
		CtTypeReference<?> typeReferenceOfDisgust = ctClassCelebrationLunch.filterChildren(new NamedElementFilter<>(CtNamedElement.class,"disgust")).map((CtTypedElement te)->{
			return te.getType();
		}).first();
		
		assertEquals("spoon.test.generics.testclasses.CelebrationLunch<java.lang.Integer, java.lang.Long, java.lang.Double>.WeddingLunch<spoon.test.generics.testclasses.Mole>",typeReferenceOfDisgust.toString());
		//method WeddingLunch#eatMe
		CtMethod<?> tWeddingLunch_eatMe = typeReferenceOfDisgust.getDeclaration().filterChildren((CtNamedElement e)->"eatMe".equals(e.getSimpleName())).first();
		
		CtClass<?> ctClassLunch = factory.Class().get(Lunch.class);
		//method Lunch#eatMe
		CtMethod<?> ctClassLunch_eatMe = ctClassLunch.filterChildren((CtNamedElement e)->"eatMe".equals(e.getSimpleName())).first();

		
		//type of first parameter of  method WeddingLunch#eatMe
		CtTypeReference<?> ctWeddingLunch_X = tWeddingLunch_eatMe.getParameters().get(0).getType();
		// X is the type parameter of WeddingLunch
		assertEquals("X", ctWeddingLunch_X.getSimpleName());
		//type of first parameter of method Lunch#eatMe
		CtTypeReference<?> ctClassLunch_A = ctClassLunch_eatMe.getParameters().get(0).getType();
		assertEquals("A", ctClassLunch_A.getSimpleName());
		
		//are these two types same?
		ClassTypingContext typingContextOfDisgust = new ClassTypingContext(typeReferenceOfDisgust);
		
		//contract: the class typing context provides its scope 
		assertSame(typeReferenceOfDisgust.getTypeDeclaration(), typingContextOfDisgust.getAdaptationScope());
		
		// in disgust, X of WeddingLunch is bound to "Model"
		assertEquals("spoon.test.generics.testclasses.Mole", typingContextOfDisgust.adaptType(ctWeddingLunch_X).getQualifiedName());
		//adapt A to scope of CelebrationLunch<Integer,Long,Double>.WeddingLunch<Mole>

		// in disgust, the A of Lunch is bound to "Mole"
		assertEquals("spoon.test.generics.testclasses.Mole", typingContextOfDisgust.adaptType(ctClassLunch_A).getQualifiedName());

		// I don't understand the goal and utility of this one
		assertEquals("java.lang.Double", typingContextOfDisgust.getEnclosingGenericTypeAdapter().adaptType(ctClassLunch_A).getQualifiedName());


		// now we resolve those types, but in the context of the declaration, where no concrete types exist
		//are these two types same in scope of CelebrationLunch<K,L,M>.WddingLunch<X> class itself
		ClassTypingContext sthOftWeddingLunch_X = new ClassTypingContext(typeReferenceOfDisgust.getDeclaration());
		
		//contract: the class typing context provides its scope 
		assertSame(typeReferenceOfDisgust.getDeclaration(), sthOftWeddingLunch_X.getAdaptationScope());
		
		// in WeddingLunch "X" is still "X"
		assertEquals("X", sthOftWeddingLunch_X.adaptType(ctWeddingLunch_X).getQualifiedName());

		// in WeddingLunch the "A" from Lunch of is called "X"
		assertEquals("X", sthOftWeddingLunch_X.adaptType(ctClassLunch_A).getQualifiedName());

		// ?????
		//adapt A to scope of enclosing class of CelebrationLunch<K,L,M>.WddingLunch<X>, which is CelebrationLunch<K,L,M>
		assertEquals("M", sthOftWeddingLunch_X.getEnclosingGenericTypeAdapter().adaptType(ctClassLunch_A).getQualifiedName());
	}
	
	@Test
	public void testRecursiveTypeAdapting() throws Exception {
		CtType<?> classOrange = buildClass(Orange.class);
		CtClass<?> classA = classOrange.getNestedType("A");
		CtTypeParameter typeParamO = classA.getFormalCtTypeParameters().get(0);
		CtTypeParameter typeParamM = classA.getFormalCtTypeParameters().get(1);
		assertEquals("O", typeParamO.getQualifiedName());
		assertEquals("M", typeParamM.getQualifiedName());
		assertEquals("K", typeParamO.getSuperclass().getQualifiedName());
		assertEquals("O", typeParamM.getSuperclass().getQualifiedName());
		assertEquals("K", typeParamM.getSuperclass().getSuperclass().getQualifiedName());
		
		CtClass<?> classB = classOrange.getNestedType("B");
		CtTypeParameter typeParamN = classB.getFormalCtTypeParameters().get(0);
		CtTypeParameter typeParamP = classB.getFormalCtTypeParameters().get(1);
		assertEquals("N", typeParamN.getQualifiedName());
		assertEquals("P", typeParamP.getQualifiedName());
		
		ClassTypingContext ctcB = new ClassTypingContext(classB);
		assertEquals("N", ctcB.adaptType(typeParamO).getQualifiedName());
		assertEquals("P", ctcB.adaptType(typeParamM).getQualifiedName());
		//contract: superClass of CtTypeParam is adapted too
		assertEquals("K", ctcB.adaptType(typeParamO).getSuperclass().getQualifiedName());
		assertEquals("N", ctcB.adaptType(typeParamM).getSuperclass().getQualifiedName());
		assertEquals("K", ctcB.adaptType(typeParamM).getSuperclass().getSuperclass().getQualifiedName());
		
		CtTypeReference<?> typeRef_list2m = classA.getField("list2m").getType();
		assertEquals("java.util.List<java.util.List<M>>", typeRef_list2m.toString());
		//contract: the CtTypeReference is adapted recursive including actual type arguments
		assertEquals("java.util.List<java.util.List<P>>", ctcB.adaptType(typeRef_list2m).toString());
		
		CtTypeReference<?> typeRef_ListQextendsM = classA.getMethodsByName("method").get(0).getParameters().get(0).getType();
		assertEquals("java.util.List<? extends M>", typeRef_ListQextendsM.toString());
		//contract: the CtTypeReference is adapted recursive including actual type arguments and their bounds
		assertEquals("java.util.List<? extends P>", ctcB.adaptType(typeRef_ListQextendsM).toString());
	}
	
	@Test
	public void testMethodTypingContext() {
		Factory factory = build(new File("src/test/java/spoon/test/generics/testclasses"));
		CtClass<?> ctClassWeddingLunch = factory.Class().get(WeddingLunch.class);
		CtMethod<?> trWeddingLunch_eatMe = ctClassWeddingLunch.filterChildren(new NamedElementFilter<>(CtMethod.class,"eatMe")).first();

		MethodTypingContext methodSTH = new MethodTypingContext().setMethod(trWeddingLunch_eatMe);

		//contract: the method typing context provides its scope 
		assertSame(trWeddingLunch_eatMe, methodSTH.getAdaptationScope());

		CtClass<?> ctClassLunch = factory.Class().get(Lunch.class);
		CtMethod<?> trLunch_eatMe = ctClassLunch.filterChildren(new NamedElementFilter<>(CtMethod.class,"eatMe")).first();
		
		CtInvocation<?> invokeReserve = factory.Class().get(CelebrationLunch.class)
				.filterChildren(new TypeFilter<>(CtInvocation.class))
				.select((CtInvocation i)->"reserve".equals(i.getExecutable().getSimpleName()))
				.first();
		
		
		MethodTypingContext methodReserveTC = new MethodTypingContext().setInvocation(invokeReserve);
		//contract: the method typing context provides its scope 
		assertSame(invokeReserve.getExecutable().getDeclaration(), methodReserveTC.getAdaptationScope());
		
		//check that MethodTypingContext made from invocation knows actual type arguments of method and all declaring types
		//1) check method actual type argument
		CtMethod<?> methodReserve = (CtMethod<?>) invokeReserve.getExecutable().getDeclaration();
		CtTypeParameter methodReserve_S = methodReserve.getFormalCtTypeParameters().get(0);
		assertEquals("S", methodReserve_S.getSimpleName());
		assertEquals("spoon.test.generics.testclasses.Tacos", methodReserveTC.adaptType(methodReserve_S).getQualifiedName());

		//2) check actual type arguments of declaring type `Section` 
		CtClass classSection = (CtClass)methodReserve.getDeclaringType();
		assertEquals("spoon.test.generics.testclasses.CelebrationLunch$WeddingLunch$Section", classSection.getQualifiedName());
		CtTypeParameter classSection_Y = classSection.getFormalCtTypeParameters().get(0);
		assertEquals("Y", classSection_Y.getSimpleName());
		assertEquals("spoon.test.generics.testclasses.Paella", methodReserveTC.adaptType(classSection_Y).getQualifiedName());
		
		//3) check actual type arguments of declaring type `WeddingLunch` 
		CtClass classWeddingLunch = (CtClass)classSection.getDeclaringType();
		assertEquals("spoon.test.generics.testclasses.CelebrationLunch$WeddingLunch", classWeddingLunch.getQualifiedName());
		CtTypeParameter classWeddingLunch_X = classWeddingLunch.getFormalCtTypeParameters().get(0);
		assertEquals("X", classWeddingLunch_X.getSimpleName());
		assertEquals("spoon.test.generics.testclasses.Mole", methodReserveTC.adaptType(classWeddingLunch_X).getQualifiedName());
		
		//4) check actual type arguments of declaring type `CelebrationLunch` 
		CtClass classCelebrationLunch = (CtClass)classWeddingLunch.getDeclaringType();
		assertEquals("spoon.test.generics.testclasses.CelebrationLunch", classCelebrationLunch.getQualifiedName());
		CtTypeParameter classCelebrationLunch_K = classCelebrationLunch.getFormalCtTypeParameters().get(0);
		CtTypeParameter classCelebrationLunch_L = classCelebrationLunch.getFormalCtTypeParameters().get(1);
		CtTypeParameter classCelebrationLunch_M = classCelebrationLunch.getFormalCtTypeParameters().get(2);
		assertEquals("K", classCelebrationLunch_K.getSimpleName());
		assertEquals("L", classCelebrationLunch_L.getSimpleName());
		assertEquals("M", classCelebrationLunch_M.getSimpleName());
		assertEquals("spoon.test.generics.testclasses.Tacos", methodReserveTC.adaptType(classCelebrationLunch_K).getQualifiedName());
		assertEquals("spoon.test.generics.testclasses.Paella", methodReserveTC.adaptType(classCelebrationLunch_L).getQualifiedName());
		assertEquals("spoon.test.generics.testclasses.Mole", methodReserveTC.adaptType(classCelebrationLunch_M).getQualifiedName());

		//method->Section->WeddingLunch->CelebrationLunch
		GenericTypeAdapter celebrationLunchTC = methodReserveTC.getEnclosingGenericTypeAdapter().getEnclosingGenericTypeAdapter().getEnclosingGenericTypeAdapter();
		assertEquals("java.lang.Integer", celebrationLunchTC.adaptType(classCelebrationLunch_K).getQualifiedName());
		assertEquals("java.lang.Long", celebrationLunchTC.adaptType(classCelebrationLunch_L).getQualifiedName());
		assertEquals("java.lang.Double", celebrationLunchTC.adaptType(classCelebrationLunch_M).getQualifiedName());
	}
	
	@Test
	public void testMethodTypingContextAdaptMethod() {
		// core contracts of MethodTypingContext#adaptMethod
		Factory factory = build(new File("src/test/java/spoon/test/generics/testclasses"));
		CtClass<?> ctClassLunch = factory.Class().get(Lunch.class);

		// represents <C> void eatMe(A paramA, B paramB, C paramC){}
		CtMethod<?> trLunch_eatMe = ctClassLunch.filterChildren(new NamedElementFilter<>(CtMethod.class,"eatMe")).first();
		CtClass<?> ctClassWeddingLunch = factory.Class().get(WeddingLunch.class);

		ClassTypingContext ctcWeddingLunch = new ClassTypingContext(ctClassWeddingLunch);
		// we all analyze new methods
		final MethodTypingContext methodSTH = new MethodTypingContext().setClassTypingContext(ctcWeddingLunch);
		//contract: method can be adapted only using MethodTypingContext
		methodSTH.setMethod(trLunch_eatMe);
		CtMethod<?> adaptedLunchEatMe = (CtMethod<?>) methodSTH.getAdaptationScope();

		//contract: adapting of method declared in different scope, returns new method
		assertNotSame(adaptedLunchEatMe, trLunch_eatMe);

		//check that new method is adapted correctly
		//is declared in correct class
		assertSame(ctClassWeddingLunch, adaptedLunchEatMe.getDeclaringType());
		//  is not member of the same class (WeddingLunch)
		for (CtTypeMember typeMember : ctClassWeddingLunch.getTypeMembers()) {
			assertNotSame(adaptedLunchEatMe, typeMember);
		}
		// the name is the same
		assertEquals("eatMe", adaptedLunchEatMe.getSimpleName());
		// it has the same number of of formal type parameters
		assertEquals(1, adaptedLunchEatMe.getFormalCtTypeParameters().size());
		assertEquals("C", adaptedLunchEatMe.getFormalCtTypeParameters().get(0).getQualifiedName());

		//parameters are correct
		assertEquals(3, adaptedLunchEatMe.getParameters().size());

		// "A paramA" becomes "X paramA" becomes Lunch%A corresponds to X in WeddingLunch
		assertEquals("X", adaptedLunchEatMe.getParameters().get(0).getType().getQualifiedName());
		// B paramB becomes Tacos becomes Lunch%B corresponds to Tacos in WeddingLunch (class WeddingLunch<X> extends CelebrationLunch<Tacos, Paella, X>)
		assertEquals(Tacos.class.getName(), adaptedLunchEatMe.getParameters().get(1).getType().getQualifiedName());
		// "C paramC" stays "C paramC"
		assertEquals("C", adaptedLunchEatMe.getParameters().get(2).getType().getQualifiedName());

		//contract: adapting of adapted method returns input method
		methodSTH.setMethod(adaptedLunchEatMe);
		assertSame(adaptedLunchEatMe, methodSTH.getAdaptationScope());
	}
	
	@Test
	public void testClassTypingContextMethodSignature() {
		// core contracts of MethodTypingContext#adaptMethod
		Factory factory = build(new File("src/test/java/spoon/test/generics/testclasses"));
		CtClass<?> ctClassLunch = factory.Class().get(Lunch.class);
		CtClass<?> ctClassWeddingLunch = factory.Class().get(WeddingLunch.class);

		// represents <C> void eatMe(A paramA, B paramB, C paramC){}
		CtMethod<?> trLunch_eatMe = ctClassLunch.filterChildren(new NamedElementFilter<>(CtMethod.class,"eatMe")).first();
		
		// represents <C> void eatMe(M paramA, K paramB, C paramC)
		CtMethod<?> trWeddingLunch_eatMe = ctClassWeddingLunch.filterChildren(new NamedElementFilter<>(CtMethod.class,"eatMe")).first();
		
		ClassTypingContext ctcWeddingLunch = new ClassTypingContext(ctClassWeddingLunch);
		
		assertTrue(ctcWeddingLunch.isOverriding(trLunch_eatMe, trLunch_eatMe));
		assertTrue(ctcWeddingLunch.isOverriding(trLunch_eatMe, trWeddingLunch_eatMe));
		assertTrue(ctcWeddingLunch.isSubSignature(trLunch_eatMe, trWeddingLunch_eatMe));

		//contract: check that adapting of methods still produces same results, even when scopeMethod is already assigned
		assertTrue(ctcWeddingLunch.isOverriding(trWeddingLunch_eatMe, trLunch_eatMe));
		assertTrue(ctcWeddingLunch.isOverriding(trWeddingLunch_eatMe, trWeddingLunch_eatMe));
		assertTrue(ctcWeddingLunch.isSubSignature(trWeddingLunch_eatMe, trWeddingLunch_eatMe));
	}
	
	
	
	@Test
	public void testClassContextOnInnerClass() throws Exception {
		CtClass<?> classBanana = (CtClass<?>)buildClass(Banana.class);
		CtClass<?> classVitamins = classBanana.getNestedType("Vitamins");
		CtTypeReference<?> refList_T = classVitamins.getSuperclass();
		//contract: generic types defined in enclocing classe (Banana<T>) are resolved from inner class hierarchy (Vitamins->List<T>) too.
		assertSame(classBanana.getFormalCtTypeParameters().get(0), new ClassTypingContext(classVitamins).adaptType(refList_T.getActualTypeArguments().get(0)).getDeclaration());
	}

	private void checkFakeTpl(CtInterface<?> fakeTplItf) {
		assertNotNull(fakeTplItf);

		CtMethod<?> applyMethod = fakeTplItf.getMethodsByName("apply").get(0);

		CtTypeReference<?> returnType = applyMethod.getType();
		assertEquals("T", returnType.getSimpleName());
		assertTrue(returnType instanceof CtTypeParameterReference);
		assertEquals("CtElement", returnType.getSuperclass().getSimpleName());

		CtParameter<?> targetType = applyMethod.getParameters().get(0);

		List<CtTypeReference<?>> targetTypeArgument = targetType.getType().getActualTypeArguments();
		assertEquals(1, targetTypeArgument.size());

		assertTrue(targetTypeArgument.get(0) instanceof CtWildcardReference);

		CtMethod<?> testMethod = fakeTplItf.getMethodsByName("test").get(0);
		List<CtParameter<?>> parameters = testMethod.getParameters();
		assertEquals(3, parameters.size());

		CtParameter thirdParam = parameters.get(2);
		assertTrue(thirdParam.getType() instanceof CtTypeParameterReference);
	}

	@Test
	public void testWildCardonShadowClass() {
		// contract: generics should be treated the same way in shadow classes

		// test that apply argument type contains a wildcard
		Launcher launcher = new Launcher();
		Factory factory = launcher.getFactory();

		launcher.addInputResource("src/test/java/spoon/test/generics/testclasses/FakeTpl.java");
		launcher.buildModel();

		CtInterface<?> fakeTplItf = factory.Interface().get("spoon.test.generics.testclasses.FakeTpl");
		checkFakeTpl(fakeTplItf);

		// same test with a shadow class
		launcher = new Launcher();
		factory = launcher.getFactory();
		CtInterface<?> fakeTplItf2 = factory.Interface().get(FakeTpl.class);
		checkFakeTpl(fakeTplItf2);
	}

	@Test
	public void testDiamondComplexGenericsRxJava() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/generics/testclasses/rxjava/");
		launcher.setSourceOutputDirectory("./target/spooned-rxjava");
		launcher.run();

		Factory factory = launcher.getFactory();

		List<CtConstructorCall> invocations = factory.getModel().getElements(new TypeFilter<>(CtConstructorCall.class));

		boolean invocationDetected = false;
		for (CtConstructorCall call : invocations) {
			if ("ToNotificationSubscriber".equals(call.getType().getSimpleName())) {
				assertEquals(1, call.getType().getActualTypeArguments().size());

				CtTypeReference actualTA = call.getType().getActualTypeArguments().get(0);
				assertTrue(actualTA instanceof CtWildcardReference);
				assertEquals("?", actualTA.getSimpleName());
				assertTrue( ((CtWildcardReference)actualTA).isDefaultBoundingType() );
				invocationDetected = true;
			}
		}

		canBeBuilt("./target/spooned-rxjava",8);

		assertTrue(invocationDetected);
	}

	@Test
	public void testGetDeclarationOfTypeParameterReference() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/generics/testclasses/ExtendedPaella.java");
		launcher.addInputResource("./src/test/java/spoon/test/generics/testclasses/Paella.java");
		launcher.buildModel();

		Factory factory = launcher.getFactory();

		CtClass extendedPaella = factory.getModel().getElements(new NamedElementFilter<>(CtClass.class,"ExtendedPaella")).get(0);
		List<CtTypeParameter> typeParameterList = extendedPaella.getFormalCtTypeParameters();

		assertEquals(1, typeParameterList.size());

		CtMethod totoMethod = factory.getModel().getElements(new NamedElementFilter<>(CtMethod.class,"toto")).get(0);
		CtTypeReference returnTypeToto = totoMethod.getType();
		CtTypeReference paramToto = ((CtParameter)totoMethod.getParameters().get(0)).getType();

		CtType declaration = returnTypeToto.getDeclaration();

		assertSame(typeParameterList.get(0), declaration);
		assertSame(typeParameterList.get(0), paramToto.getDeclaration());

		CtMethod machinMethod = factory.getModel().getElements(new NamedElementFilter<>(CtMethod.class,"machin")).get(0);
		CtTypeReference returnTypeMachin = machinMethod.getType();
		List<CtTypeParameter> formalCtTypeParameters = machinMethod.getFormalCtTypeParameters();

		assertEquals(1, formalCtTypeParameters.size());

		CtType declarationMachin = returnTypeMachin.getDeclaration();

		assertNotSame(typeParameterList.get(0), declarationMachin);
		assertSame(formalCtTypeParameters.get(0), declarationMachin);

		CtClass innerPaella = factory.getModel().getElements(new NamedElementFilter<>(CtClass.class,"InnerPaella")).get(0);
		List<CtTypeParameter> innerTypeParametersList = innerPaella.getFormalCtTypeParameters();

		assertEquals(typeParameterList.get(0), innerTypeParametersList.get(0).getSuperclass().getDeclaration());

		CtMethod innerMachinMethod = factory.getModel().getElements(new NamedElementFilter<>(CtMethod.class,"innerMachin")).get(0);
		CtTypeReference returnTypeInnerMachin = innerMachinMethod.getType();
		CtTypeReference paramInnerMachinType = ((CtParameter)innerMachinMethod.getParameters().get(0)).getType();
		List<CtTypeParameter> innerMachinFormalCtType = innerMachinMethod.getFormalCtTypeParameters();

		assertSame(typeParameterList.get(0), returnTypeInnerMachin.getDeclaration());
		assertSame(innerMachinFormalCtType.get(0), paramInnerMachinType.getDeclaration());

		CtMethod innerTotoMethod = factory.getModel().getElements(new NamedElementFilter<>(CtMethod.class,"innerToto")).get(0);
		CtTypeReference returnInnerToto = innerTotoMethod.getType();
		CtTypeReference paramInnerToto = ((CtParameter)innerTotoMethod.getParameters().get(0)).getType();
		List<CtTypeParameter> innerTotoFormatCtType = innerTotoMethod.getFormalCtTypeParameters();

		assertSame(innerTotoFormatCtType.get(0), paramInnerToto.getDeclaration());
		assertSame(innerTypeParametersList.get(0), returnInnerToto.getDeclaration());
	}

	@Test
	public void testIsSameSignatureWithGenerics() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/generics/testclasses/SameSignature.java");
		launcher.buildModel();

		CtClass ctClass = launcher.getFactory().Class().get(SameSignature.class);

		List<CtMethod> methods = ctClass.getMethodsByName("forEach");
		assertEquals(1, methods.size());

		CtType<?> iterableItf = launcher.getFactory().Type().get(Iterable.class);

		List<CtMethod<?>> methodsItf = iterableItf.getMethodsByName("forEach");
		assertEquals(1, methodsItf.size());

		ClassTypingContext ctc = new ClassTypingContext(ctClass.getReference());
		assertTrue(ctc.isOverriding(methods.get(0), methodsItf.get(0)));
		assertTrue(ctc.isSubSignature(methods.get(0), methodsItf.get(0)));
		assertTrue(ctc.isSameSignature(methods.get(0), methodsItf.get(0)));
	}

	@Test
	public void testIsSameSignatureWithMethodGenerics() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/generics/testclasses2/SameSignature2.java");
		launcher.buildModel();

		CtClass ctClass = launcher.getFactory().Class().get(SameSignature2.class);
		CtMethod classMethod = (CtMethod)ctClass.getMethodsByName("visitCtConditional").get(0);

		CtType<?> iface = launcher.getFactory().Type().get("spoon.test.generics.testclasses2.ISameSignature");
		CtMethod ifaceMethod = iface.getMethodsByName("visitCtConditional").get(0);

		ClassTypingContext ctcSub = new ClassTypingContext(ctClass.getReference());
		assertTrue(ctcSub.isOverriding(classMethod, ifaceMethod));
		assertTrue(ctcSub.isOverriding(ifaceMethod, classMethod));
		assertTrue(ctcSub.isSubSignature(classMethod, ifaceMethod));
		assertTrue(ctcSub.isSubSignature(ifaceMethod, classMethod));
		assertTrue(ctcSub.isSameSignature(classMethod, ifaceMethod));
		assertTrue(ctcSub.isSameSignature(ifaceMethod, classMethod));
	}
	
	@Test
	public void testGetExecDeclarationOfEnumSetOf() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/generics/testclasses/EnumSetOf.java");
		launcher.buildModel();

		CtClass<?> ctClass = launcher.getFactory().Class().get(EnumSetOf.class);

		CtInvocation invocation = ctClass.getMethodsByName("m").get(0).getBody().getStatement(0);
		CtExecutable<?> decl = invocation.getExecutable().getDeclaration();
		assertNull(decl);

		CtClass<?> enumClass = launcher.getFactory().Class().get(EnumSet.class);
		List<CtMethod<?>> methods = enumClass.getMethodsByName("of");

		CtMethod rightOfMethod = null;
		for (CtMethod method : methods) {
			if (method.getParameters().size() == 1) {
				rightOfMethod = method;
			}
		}

		assertNotNull(rightOfMethod);

		decl = invocation.getExecutable().getExecutableDeclaration();
		assertEquals(rightOfMethod, decl);
	}
	
	@Test
	public void testIsSameSignatureWithReferencedGenerics() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/generics/testclasses2/SameSignature3.java");
		launcher.buildModel();

		CtClass ctClass = launcher.getFactory().Class().get(SameSignature3.class);
		CtMethod classMethod = (CtMethod)ctClass.getMethodsByName("visitCtConditional").get(0);

		CtType<?> iface = launcher.getFactory().Type().get("spoon.test.generics.testclasses2.ISameSignature3");
		CtMethod ifaceMethod = (CtMethod)iface.getMethodsByName("visitCtConditional").get(0);

		ClassTypingContext ctcSub = new ClassTypingContext(ctClass.getReference());
		assertTrue(ctcSub.isOverriding(classMethod, ifaceMethod));
		assertTrue(ctcSub.isOverriding(ifaceMethod, classMethod));
		assertTrue(ctcSub.isSubSignature(classMethod, ifaceMethod));
		assertTrue(ctcSub.isSubSignature(ifaceMethod, classMethod));
		assertTrue(ctcSub.isSameSignature(classMethod, ifaceMethod));
		assertTrue(ctcSub.isSameSignature(ifaceMethod, classMethod));
	}
	
	@Test
	public void testIsGenericTypeEqual() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/generics/testclasses2/LikeCtClass.java");
		launcher.addInputResource("./src/test/java/spoon/test/generics/testclasses2/LikeCtClassImpl.java");
		launcher.buildModel();

		CtType<?> ctIFace = launcher.getFactory().Interface().get(LikeCtClass.class);
		CtMethod<?> ifaceGetter = (CtMethod)ctIFace.getMethodsByName("getConstructors").get(0);
		CtMethod<?> ifaceSetter = (CtMethod)ctIFace.getMethodsByName("setConstructors").get(0);
		assertEquals(ifaceGetter.getType().toString(), ifaceSetter.getParameters().get(0).getType().toString());
		assertEquals(ifaceGetter.getType(), ifaceSetter.getParameters().get(0).getType());
		
		CtType<?> ctClass = launcher.getFactory().Class().get(LikeCtClassImpl.class);
		CtMethod<?> classGetter = (CtMethod)ctClass.getMethodsByName("getConstructors").get(0);
		CtMethod<?> classSetter = (CtMethod)ctClass.getMethodsByName("setConstructors").get(0);
		assertEquals(classGetter.getType().toString(), classSetter.getParameters().get(0).getType().toString());
		assertEquals(classGetter.getType(), classSetter.getParameters().get(0).getType());
		
		assertEquals(ifaceGetter.getType().toString(), classGetter.getType().toString());
		assertEquals(ifaceGetter.getType(), classGetter.getType());
		assertEquals(ifaceSetter.getParameters().get(0).getType().toString(), classSetter.getParameters().get(0).getType().toString());
		assertEquals(ifaceSetter.getParameters().get(0).getType(), classSetter.getParameters().get(0).getType());
		
		assertEquals(ifaceSetter.getParameters().get(0).getType(), classGetter.getType());
		
		MethodTypingContext mtc = new MethodTypingContext().setClassTypingContext(new ClassTypingContext(ctClass)).setMethod(ifaceSetter);
		CtMethod<?> adaptedMethod = (CtMethod<?>) mtc.getAdaptationScope();
		/*
		 * after adaptation of `Set<AnType<T>>` from scope of interface to scope of class there is Set<AnType<T extends Object>>
		 * Which is semantically equivalent, but Equals check does not know that
		 */
		assertEquals(adaptedMethod.getParameters().get(0).getType(), classGetter.getType());
		assertEquals(adaptedMethod.getParameters().get(0).getType(), classSetter.getParameters().get(0).getType());

		new ContractVerifier(launcher.getFactory().getModel().getRootPackage()).checkParentConsistency();
		new ContractVerifier().checkParentConsistency(adaptedMethod);
	}

	@Test
	public void testCannotAdaptTypeOfNonTypeScope() throws Exception {
		//contract: ClassTypingContext doesn't fail on type parameters, which are defined out of the scope of ClassTypingContext
		CtType<?> ctClass = buildClass(OuterTypeParameter.class);
		//the method defines type parameter, which is used in super of local class
		CtReturn<?> retStmt = (CtReturn<?>) ctClass.getMethodsByName("method").get(0).getBody().getStatements().get(0);
		CtNewClass<?> newClassExpr = (CtNewClass<?>) retStmt.getReturnedExpression();
		CtType<?> declaringType = newClassExpr.getAnonymousClass();
		CtMethod<?> m1 = declaringType.getMethodsByName("iterator").get(0);
		ClassTypingContext c = new ClassTypingContext(declaringType);
		//the adaptation of such type parameter keeps that parameter as it is.
		assertFalse(c.isOverriding(m1, declaringType.getSuperclass().getTypeDeclaration().getMethodsByName("add").get(0)));
		assertTrue(c.isOverriding(m1, declaringType.getSuperclass().getTypeDeclaration().getMethodsByName("iterator").get(0)));
	}

	@Test
	public void testGenericsOverriding() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/generics/testclasses4/A.java");
		CtModel model = launcher.buildModel();

		CtClass<?> a = model.getElements(new NamedElementFilter<>(CtClass.class, "A")).get(0);
		CtClass<?> b = model.getElements(new NamedElementFilter<>(CtClass.class, "B")).get(0);

		CtMethod m6A = a.getMethodsByName("m6").get(0);
		CtMethod m6B = b.getMethodsByName("m6").get(0);

		assertTrue(m6B.isOverriding(m6A));
	}

	@Test
	public void testDeepGenericsInExecutableReference() {
		//the generic which extends another generic is handled well
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/generics/testclasses4/C.java");
		CtModel model = launcher.buildModel();

		CtClass<?> c = model.getElements(new NamedElementFilter<>(CtClass.class, "C")).get(0);

		
		CtMethod<?> m = c.getMethodsByName("m").get(0);
		CtTypeReference<?> execParamType = m.getParameters().get(0).getType();
		assertEquals(CtTypeParameterReferenceImpl.class, execParamType.getClass());
		assertEquals("W", execParamType.getSimpleName());
		
		{
			CtExecutableReference<?> mRef = ((CtInvocation) m.getBody().getStatements().get(0)).getExecutable();
	
			CtTypeReference<?> execRefParamType = mRef.getParameters().get(0);
			assertEquals(CtTypeReferenceImpl.class, execRefParamType.getClass());
			assertEquals("java.util.List<java.lang.String>", execRefParamType.toString());
		}
		{
			CtExecutableReference<?> mRef = m.getReference();
	
			CtTypeReference<?> execRefParamType = mRef.getParameters().get(0);
			assertEquals(CtTypeReferenceImpl.class, execRefParamType.getClass());
			assertEquals("java.util.List<java.lang.String>", execRefParamType.toString());
		}
	}

	@Test
	public void testTopLevelIsGenerics() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/generics/testclasses/Banana.java");
		launcher.addInputResource("./src/test/java/spoon/test/generics/testclasses/Mole.java");
		CtModel model = launcher.buildModel();
		CtType<?> banana = model.getAllTypes().stream().filter(t -> t.getSimpleName().equals("Banana")).findFirst().get();
		CtType<?> mole = model.getAllTypes().stream().filter(t -> t.getSimpleName().equals("Mole")).findFirst().get();
		assertTrue(banana.isGenerics());
		assertFalse(mole.isGenerics());
	}

	@Test
	public void testIsParameterized() {
		// contract: isParameterized should work as expected
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/generics/testclasses5/A.java");
		launcher.addInputResource("./src/test/java/spoon/test/generics/testclasses5/B.java");
		CtModel model = launcher.buildModel();

		CtType<?> a = model.getElements(new NamedElementFilter<>(CtType.class, "A")).get(0);
		CtType<?> b = model.getElements(new NamedElementFilter<>(CtType.class, "B")).get(0);
		assertTrue(a.isParameterized());
		assertFalse(b.isParameterized());

		CtMethod<?> m1 = a.getMethodsByName("m1").get(0);
		CtMethod<?> m2 = a.getMethodsByName("m2").get(0);
		CtMethod<?> m3 = a.getMethodsByName("m3").get(0);

		assertFalse(m2.getFormalCtTypeParameters().get(0).isParameterized());
		assertFalse(m3.getFormalCtTypeParameters().get(0).isParameterized());

		CtParameter<?> param1 = m1.getParameters().get(0);
		CtParameter<?> param2 = m1.getParameters().get(1);
		CtParameter<?> param3 = m1.getParameters().get(2);
		CtParameter<?> param4 = m1.getParameters().get(3);
		assertTrue(param1.getType().isParameterized());
		assertTrue(param2.getType().isParameterized());
		assertTrue(param3.getType().isParameterized());
		assertFalse(param4.getType().isParameterized());

		assertFalse(param1.getType().getActualTypeArguments().get(0).isParameterized());
		assertFalse(param2.getType().getActualTypeArguments().get(0).isParameterized());
		assertTrue(param3.getType().getActualTypeArguments().get(0).isParameterized());
	}

	@Test
	public void testExecutableTypeParameter() {
		// contract: getTypeParameterDeclaration() should not produce class cast exception
		// https://github.com/INRIA/spoon/issues/3040
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/generics/testclasses6/A.java");
		CtModel model = launcher.buildModel();
		CtInvocation m1 = model.getElements(new TypeFilter<>(CtInvocation.class)).get(1);
		CtTypeParameter formalType = ((CtMethod) m1.getExecutable().getDeclaration()).getFormalCtTypeParameters().get(0);
		assertEquals(formalType, ((CtTypeReference) m1.getActualTypeArguments().get(0)).getTypeParameterDeclaration());
		assertNull(m1.getType().getTypeParameterDeclaration());
	}
}
