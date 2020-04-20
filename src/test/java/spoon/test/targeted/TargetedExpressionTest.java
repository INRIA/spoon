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
package spoon.test.targeted;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.buildClass;

import java.util.List;

import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.comparator.CtLineElementComparator;
import spoon.support.reflect.code.CtConstructorCallImpl;
import spoon.support.reflect.code.CtFieldReadImpl;
import spoon.support.reflect.code.CtThisAccessImpl;
import spoon.support.util.SortedList;
import spoon.test.targeted.testclasses.Bar;
import spoon.test.targeted.testclasses.Foo;
import spoon.test.targeted.testclasses.InternalSuperCall;
import spoon.test.targeted.testclasses.Pozole;
import spoon.test.targeted.testclasses.SuperClass;
import spoon.test.targeted.testclasses.Tapas;


public class TargetedExpressionTest {
	@Test
	public void testCtSuperAccess() throws Exception {
		final Factory factory = build(InternalSuperCall.class);
		final CtClass<?> ctClass = factory.Class().get(InternalSuperCall.class);

		CtMethod<?> method = ctClass.getElements(new NamedElementFilter<>(CtMethod.class,"methode")).get(0);
		assertEquals(
				"spoon.test.targeted.testclasses.InternalSuperCall.super.toString()",
				method.getBody().getStatements().get(0).toString());
		assertNotNull(method.getElements(new TypeFilter<>(CtSuperAccess.class)).get(0).getTarget());

		CtMethod<?> toStringMethod = ctClass.getElements(new NamedElementFilter<>(CtMethod.class,"toString")).get(0);
		assertEquals(
				"return super.toString()",
				toStringMethod.getBody().getStatements().get(0).toString());
		assertNull(toStringMethod.getElements(new TypeFilter<>(CtSuperAccess.class)).get(0).getTarget());
	}

	@Test
	public void testCtThisAccess() throws Exception {
		CtType<?> type = build("spoon.test.targeted.testclasses", "InnerClassThisAccess");
		assertEquals("InnerClassThisAccess", type.getSimpleName());

		CtMethod<?> meth1 = type.getElements(new NamedElementFilter<>(CtMethod.class,"method2")).get(0);
		assertEquals(
				"this.method()",
				meth1.getBody().getStatements().get(0).toString());

		CtClass<?> c = type.getElements(new NamedElementFilter<>(CtClass.class,"1InnerClass")).get(0);
		assertEquals("1InnerClass", c.getSimpleName());
		CtConstructor<?> ctr = c.getConstructor(type.getFactory().Type().createReference(boolean.class));
		assertEquals("this.b = b", ctr.getBody().getLastStatement().toString());
	}

	@Test
	public void testTargetOfFieldAccess() throws Exception {
		Factory factory = build(Foo.class, Bar.class, SuperClass.class);
		final CtClass<Object> type = factory.Class().get(Foo.class);
		CtConstructor<?> constructor = type.getConstructors().toArray(new CtConstructor<?>[0])[0];

		final List<CtFieldAccess<?>> elements = constructor.getElements(new TypeFilter<>(CtFieldAccess.class));
		assertEquals(2, elements.size());

		assertSame("Target is CtThisAccessImpl if there is a 'this' explicit.", CtThisAccessImpl.class, elements.get(0).getTarget().getClass());
		assertNotNull("Target isn't null if there is a 'this' explicit.", elements.get(1).getTarget());
		assertTrue(elements.get(1).getTarget().isImplicit());
	}

	@Test
	public void testNotTargetedExpression() throws Exception {
		Factory factory = build(Foo.class, Bar.class, SuperClass.class);
		CtClass<Object> fooClass = factory.Class().get(Foo.class);
		CtField<?> iField = fooClass.getField("i");
		CtFieldAccess<?> fieldAccess = factory.Core().createFieldRead();
		fieldAccess.setVariable((CtFieldReference) iField.getReference());
		fieldAccess.setTarget(factory.Code().createThisAccess(fooClass.getReference()));
		assertEquals("this.i", fieldAccess.toString());
		// this test is made for this line. Check that we can setTarget(null)
		// without NPE
		fieldAccess.setTarget(null);
		assertEquals("i", fieldAccess.toString());
	}

	@Test
	public void testCastWriteWithGenerics() throws Exception {
		final Factory factory = build(Pozole.class);
		final CtClass<Object> aPozole = factory.Class().get(Pozole.class);
		final CtConstructor<Object> aConstructor = aPozole.getConstructor(aPozole.getReference());

		final List<CtFieldRead> elements = aConstructor.getElements(new TypeFilter<>(CtFieldRead.class));
		assertEquals(1, elements.size());
		assertEquals("((spoon.test.targeted.testclasses.Pozole<T>) (v1))", elements.get(0).getTarget().toString());
	}

	@Test
	public void testTargetsOfFieldAccess() throws Exception {
		final Factory factory = build(Foo.class, Bar.class, SuperClass.class);
		final CtClass<Foo> type = factory.Class().get(Foo.class);
		final CtTypeReference<Foo> expectedType = type.getReference();
		final CtTypeReference<Bar> expectedBarType = factory.Class().<Bar>get(Bar.class).getReference();
		final CtTypeReference<SuperClass> expectedSuperClassType = factory.Class().<SuperClass>get(SuperClass.class).getReference();
		final CtTypeReference<Foo.Fii.Fuu> expectedFuuType = factory.Class().<Foo.Fii.Fuu>get(Foo.Fii.Fuu.class).getReference();
		final CtMethod<?> fieldMethod = type.getMethodsByName("field").get(0);

		final CtThisAccess<Foo> expectedThisAccess = type.getFactory().Code().createThisAccess(expectedType);

		final List<CtFieldAccess<?>> elements = fieldMethod.getElements(new TypeFilter<>(CtFieldAccess.class));
		assertEquals(10, elements.size());
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedType).target(expectedThisAccess).result("this.i"), elements.get(0));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedType).target(expectedThisAccess).result("i"), elements.get(1));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedBarType).target(elements.get(3)).result("this.bar.i"), elements.get(2));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedType).target(expectedThisAccess).result("this.bar"), elements.get(3));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedBarType).target(elements.get(5)).result("bar.i"), elements.get(4));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedType).target(expectedThisAccess).result("bar"), elements.get(5));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedSuperClassType).target(expectedThisAccess).result("this.o"), elements.get(6));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedSuperClassType).target(expectedThisAccess).result("o"), elements.get(7));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedFuuType).target(elements.get(9)).result("fuu.p"), elements.get(8));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedType).target(expectedThisAccess).result("fuu"), elements.get(9));
	}

	@Test
	public void testTargetsOfStaticFieldAccess() throws Exception {
		final Factory factory = build(Foo.class, Bar.class, SuperClass.class);
		final CtClass<Foo> type = factory.Class().get(Foo.class);
		final CtTypeReference<Foo> expectedType = type.getReference();
		final CtTypeReference<Bar> expectedBarType = factory.Class().<Bar>get(Bar.class).getReference();
		final CtMethod<?> constructor = type.getMethodsByName("m").get(0);

		final CtThisAccess<Foo> expectedThisAccess = type.getFactory().Code().createThisAccess(expectedType);
		final CtTypeAccess<Foo> expectedTypeAccess = type.getFactory().Code().createTypeAccess(expectedType);
		final CtTypeAccess<Bar> expectedBarTypeAccess = type.getFactory().Code().createTypeAccess(expectedBarType);

		final List<CtFieldAccess<?>> elements = constructor.getElements(new TypeFilter<>(CtFieldAccess.class));
		assertEquals(10, elements.size());
		assertEqualsFieldAccess(new ExpectedTargetedExpression().type(CtFieldRead.class).declaringType(expectedType).target(expectedThisAccess).result("this.k"), elements.get(0));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().type(CtFieldRead.class).declaringType(expectedType).target(expectedTypeAccess).result("spoon.test.targeted.testclasses.Foo.k"), elements.get(1));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().type(CtFieldRead.class).declaringType(expectedType).target(expectedTypeAccess).result("spoon.test.targeted.testclasses.Foo.k"), elements.get(2));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().type(CtFieldWrite.class).declaringType(expectedType).target(expectedThisAccess).result("this.k"), elements.get(3));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().type(CtFieldWrite.class).declaringType(expectedType).target(expectedTypeAccess).result("spoon.test.targeted.testclasses.Foo.k"), elements.get(4));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().type(CtFieldWrite.class).declaringType(expectedType).target(expectedTypeAccess).result("spoon.test.targeted.testclasses.Foo.k"), elements.get(5));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().type(CtFieldRead.class).declaringType(expectedBarType).target(expectedBarTypeAccess).result("spoon.test.targeted.testclasses.Bar.FIELD"), elements.get(6));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().type(CtFieldRead.class).declaringType(expectedBarType).target(expectedBarTypeAccess).result("spoon.test.targeted.testclasses.Bar.FIELD"), elements.get(7));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().type(CtFieldWrite.class).declaringType(expectedBarType).target(expectedBarTypeAccess).result("spoon.test.targeted.testclasses.Bar.FIELD"), elements.get(8));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().type(CtFieldWrite.class).declaringType(expectedBarType).target(expectedBarTypeAccess).result("spoon.test.targeted.testclasses.Bar.FIELD"), elements.get(9));

		final CtAnonymousExecutable staticInit = type.getAnonymousExecutables().get(0);
		final List<CtFieldAccess<?>> staticElements = staticInit.getElements(new TypeFilter<>(CtFieldAccess.class));
		assertEquals(1, staticElements.size());

		// contract: when accessing a static field from a block, it is written short (not fully qualified)
		// otherwise this resulting code does not compile
		assertEqualsFieldAccess(new ExpectedTargetedExpression().type(CtFieldWrite.class).declaringType(expectedType).target(expectedTypeAccess).result("p"), staticElements.get(0));
	}

	@Test
	public void testTargetsOfFieldAccessInInnerClass() throws Exception {
		final Factory factory = build(Foo.class, Bar.class, SuperClass.class);
		final CtClass<Foo> type = factory.Class().get(Foo.class);
		final CtTypeReference<Foo> expectedType = type.getReference();
		final CtTypeReference<SuperClass> expectedSuperClassType = factory.Class().<SuperClass>get(SuperClass.class).getReference();
		final CtType<?> innerClass = type.getNestedType("InnerClass");
		final CtTypeReference<?> expectedInnerClass = innerClass.getReference();
		final CtType<?> nestedTypeScanner = type.getNestedType("1NestedTypeScanner");
		final CtTypeReference<?> expectedNested = nestedTypeScanner.getReference();

		final CtTypeAccess<Foo> fooTypeAccess = factory.Code().createTypeAccess(expectedType);
		final CtThisAccess<Foo> expectedThisAccess = factory.Code().createThisAccess(expectedType);
		final CtThisAccess<?> expectedInnerClassAccess = factory.Code().createThisAccess(expectedInnerClass);
		final CtThisAccess expectedNestedAccess = factory.Code().createThisAccess(expectedNested);

		final CtMethod<?> innerInvMethod = innerClass.getMethodsByName("innerField").get(0);
		final List<CtFieldAccess<?>> elements = innerInvMethod.getElements(new TypeFilter<>(CtFieldAccess.class));
		assertEquals(6, elements.size());
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedInnerClass).target(expectedInnerClassAccess).result("this.i"), elements.get(0));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedInnerClass).target(expectedInnerClassAccess).result("i"), elements.get(1));
		assertTrue(elements.get(1).getTarget().isImplicit());
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedType).target(expectedThisAccess).result("this.i"), elements.get(2));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedType).target(fooTypeAccess).result("spoon.test.targeted.testclasses.Foo.k"), elements.get(3));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedSuperClassType).target(expectedThisAccess).result("this.o"), elements.get(4));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedSuperClassType).target(expectedThisAccess).result("o"), elements.get(5));

		final List<CtFieldAccess<?>> newElements = nestedTypeScanner.getMethodsByName("checkField").get(0).getElements(new TypeFilter<>(CtFieldAccess.class));
		assertEquals(2, newElements.size());
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedNested).target(expectedNestedAccess).result("this.type").isLocal(), newElements.get(0));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedNested).target(expectedNestedAccess).result("type").isLocal(), newElements.get(1));
	}

	@Test
	public void testTargetsOfFieldInAnonymousClass() throws Exception {
		final Factory factory = build(Foo.class, Bar.class, SuperClass.class);
		final CtClass<Foo> type = factory.Class().get(Foo.class);
		final CtTypeReference<Foo> expectedType = type.getReference();
		final CtClass<?> anonymousClass = type.getElements(new TypeFilter<CtClass>(CtClass.class) {
			@Override
			public boolean matches(CtClass element) {
				return element.isAnonymous() && super.matches(element);
			}
		}).get(0);
		final CtTypeReference<?> expectedAnonymousType = anonymousClass.getReference();
		final CtThisAccess<Foo> expectedThisAccess = factory.Code().createThisAccess(expectedType);
		final CtThisAccess expectedAnonymousThisAccess = factory.Code().createThisAccess(expectedAnonymousType);

		final CtMethod<?> method = anonymousClass.getMethodsByName("invStatic").get(0);
		final List<CtFieldAccess> elements = method.getElements(new TypeFilter<>(CtFieldAccess.class));
		assertEquals(3, elements.size());
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedType).target(expectedThisAccess).result("this.i"), elements.get(0));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedAnonymousType).target(expectedAnonymousThisAccess).result("this.i"), elements.get(1));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedAnonymousType).target(expectedAnonymousThisAccess).result("i"), elements.get(2));
	}

	@Test
	public void testStaticTargetsOfFieldAccessNoClasspath() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/spoon/test/noclasspath/targeted/Foo.java");
		launcher.setSourceOutputDirectory("./target/noclasspath");
		launcher.run();

		final CtTypeReference<Object> expectedFoo = launcher.getFactory().Class().createReference("Foo");
		final CtTypeReference<Object> expectedBar = launcher.getFactory().Class().createReference("Bar");
		final CtTypeReference<Object> expectedFiiFuu = launcher.getFactory().Class().create("Fii.Fuu").getReference();
		final CtThisAccess<Object> expectedThisAccess = launcher.getFactory().Code().createThisAccess(expectedFoo);
		final CtTypeAccess<Object> expectedTypeAccess = launcher.getFactory().Code().createTypeAccess(expectedFoo);
		final CtTypeAccess<Object> expectedBarTypeAccess = launcher.getFactory().Code().createTypeAccess(expectedBar);

		final CtMethod<?> fieldMethod = launcher.getFactory().Class().get("Foo").getMethodsByName("field").get(0);
		final List<CtFieldAccess<?>> elements = fieldMethod.getElements(new TypeFilter<>(CtFieldAccess.class));
		assertEquals(10, elements.size());
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedFoo).target(CtConstructorCallImpl.class).result("new Foo().i"), elements.get(0));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedFoo).target(elements.get(2)).result("foo.i"), elements.get(1));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedFoo).target(expectedThisAccess).result("foo"), elements.get(2));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedFoo).target(expectedThisAccess).result("this.i"), elements.get(3));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedFoo).target(expectedThisAccess).result("foo"), elements.get(4));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedFoo).target(expectedTypeAccess.toString()).result("Foo.staticField"), elements.get(5));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().result("staticField"), elements.get(6));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedBar).target(expectedBarTypeAccess).result("Bar.staticFieldBar"), elements.get(7));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedBar).target(expectedBarTypeAccess).result("Bar.staticFieldBar"), elements.get(8));
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedFiiFuu).target(launcher.getFactory().Code().createTypeAccess(expectedFiiFuu)).result("Fii.Fuu.i"), elements.get(9));
	}

	@Test
	public void testOnlyStaticTargetFieldReadNoClasspath() {
		// bug case kindly provided by @slarse
		// in https://github.com/INRIA/spoon/issues/3329
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/spoon/test/noclasspath/targeted/StaticFieldReadOnly.java");
		CtModel model = launcher.buildModel();

		List<CtInvocation<?>> invocations = model.getElements(e -> e.getExecutable().getSimpleName().equals("error"));
		CtInvocation<?> inv = invocations.get(0);
		CtFieldRead<?> fieldRead = (CtFieldRead<?>) inv.getTarget();
		CtExpression<?> target = fieldRead.getTarget();

		assertTrue(target instanceof CtTypeAccess);
		assertEquals("Launcher", ((CtTypeAccess<?>) target).getAccessedType().getSimpleName());
	}

	@Test
	public void testNestedClassAccessEnclosingTypeFieldNoClasspath() {
		// Checks that a nested class accessing a field of an enclosing type's non-static field correctly
		// resolves to a non-static field access. See https://github.com/INRIA/spoon/issues/3334 for details.
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/spoon/test/noclasspath/targeted/Outer.java");
		CtModel model = launcher.buildModel();

		List<CtFieldRead<?>> fieldReads = model.getElements(e -> e.getVariable().getSimpleName().equals("cls"));
		assertEquals(1, fieldReads.size());
		CtFieldRead<?> fieldRead = fieldReads.get(0);

		assertTrue(fieldRead.getTarget() instanceof CtThisAccess);
	}

	@Test
	public void testTargetsOfInv() throws Exception {
		// contract: Specify declaring type of the executable of an invocation, the target of the invocation and its result.
		final Factory factory = build(Foo.class, Bar.class, SuperClass.class);
		final CtClass<Foo> type = factory.Class().get(Foo.class);
		final CtClass<Foo.Fii.Fuu> fuu = factory.Class().<Foo.Fii.Fuu>get(Foo.Fii.Fuu.class);
		final CtTypeReference<Foo> expectedType = type.getReference();
		final CtTypeReference<Bar> expectedBarType = factory.Class().<Bar>get(Bar.class).getReference();
		final CtTypeReference<SuperClass> expectedSuperClassType = factory.Class().<SuperClass>get(SuperClass.class).getReference();
		final CtTypeReference<Foo.Fii.Fuu> expectedFuuType = fuu.getReference();

		final CtThisAccess<Foo> expectedThisAccess = factory.Code().createThisAccess(expectedType);
		final CtTypeAccess<Foo> fooTypeAccess = factory.Code().createTypeAccess(expectedType);
		final CtTypeAccess<SuperClass> superClassTypeAccess = factory.Code().createTypeAccess(expectedSuperClassType);
		final CtThisAccess<Foo> expectedSuperThisAccess = factory.Code().createThisAccess(expectedType);
		expectedSuperThisAccess.setTarget(superClassTypeAccess);

		final List<CtInvocation<?>> elements = type.getMethodsByName("inv").get(0).getElements(new TypeFilter<>(CtInvocation.class));
		assertEquals(7, elements.size());

		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedType).target(CtConstructorCallImpl.class).result("new spoon.test.targeted.testclasses.Foo(0, 0).method()"), elements.get(0));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedType).target(CtFieldReadImpl.class).result("foo.method()"), elements.get(1));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedType).target(expectedThisAccess).result("this.method()"), elements.get(2));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedType).target(expectedThisAccess).result("method()"), elements.get(3));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedBarType).target(CtFieldReadImpl.class).result("bar.methodBar()"), elements.get(4));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedFuuType).target(CtFieldReadImpl.class).result("fuu.method()"), elements.get(5));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedSuperClassType).target(expectedSuperThisAccess).result("superMethod()"), elements.get(6));

		assertEquals(fooTypeAccess.getType().getQualifiedName(), ((CtThisAccess) elements.get(2).getTarget()).getTarget().getType().getQualifiedName());
		assertEquals(fooTypeAccess.getType().getQualifiedName(), ((CtThisAccess) elements.get(3).getTarget()).getTarget().getType().getQualifiedName());
	}

	@Test
	public void testStaticTargetsOfInv() throws Exception {
		// contract: Specify declaring type of the executable of an static invocation, the target of the static invocation and its result.
		final Factory factory = build(Foo.class, Bar.class, SuperClass.class);
		final CtClass<Foo> type = factory.Class().get(Foo.class);
		final CtClass<Foo.Fii.Fuu> fuu = factory.Class().<Foo.Fii.Fuu>get(Foo.Fii.Fuu.class);
		final CtTypeReference<Foo> expectedType = type.getReference();
		final CtTypeReference<Bar> expectedBarType = factory.Class().<Bar>get(Bar.class).getReference();
		final CtTypeReference<Foo.Fii.Fuu> expectedFuuType = fuu.getReference();

		final CtThisAccess<Foo> expectedThisAccess = type.getFactory().Code().createThisAccess(expectedType);
		final CtTypeAccess<Foo> expectedTypeAccess = type.getFactory().Code().createTypeAccess(expectedType);
		final CtTypeAccess<Bar> expectedBarTypeAccess = type.getFactory().Code().createTypeAccess(expectedBarType);

		final CtMethod<?> invMethod = type.getMethodsByName("invStatic").get(0);
		final List<CtInvocation<?>> elements = invMethod.getElements(new TypeFilter<>(CtInvocation.class));
		assertEquals(8, elements.size());
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedType).target(CtConstructorCallImpl.class).result("new spoon.test.targeted.testclasses.Foo(0, 0).staticMethod()"), elements.get(0));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedType).target(CtFieldReadImpl.class).result("foo.staticMethod()"), elements.get(1));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedType).target(expectedThisAccess).result("this.staticMethod()"), elements.get(2));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedType).target(expectedTypeAccess).result("spoon.test.targeted.testclasses.Foo.staticMethod()"), elements.get(3));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedType).target(expectedTypeAccess).result("spoon.test.targeted.testclasses.Foo.staticMethod()"), elements.get(4));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedBarType).target(expectedBarTypeAccess).result("spoon.test.targeted.testclasses.Bar.staticMethodBar()"), elements.get(5));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedBarType).target(expectedBarTypeAccess).result("spoon.test.targeted.testclasses.Bar.staticMethodBar()"), elements.get(6));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedFuuType).target(factory.Code().createTypeAccess(expectedFuuType)).result("spoon.test.targeted.testclasses.Foo.Fii.Fuu.m()"), elements.get(7));
	}

	@Test
	public void testTargetsOfInvInInnerClass() throws Exception {
		// contract: Specify declaring type of the executable of an invocation, the target of the invocation and its result. All this in an innerclass.
		final Factory factory = build(Foo.class, Bar.class, SuperClass.class);
		final CtClass<Foo> type = factory.Class().get(Foo.class);
		final CtTypeReference<Foo> expectedType = type.getReference();
		final CtTypeReference<SuperClass> expectedSuperClassType = factory.Class().<SuperClass>get(SuperClass.class).getReference();
		final CtType<?> innerClass = type.getNestedType("InnerClass");
		final CtTypeReference<?> expectedInnerClass = innerClass.getReference();
		final CtType<?> nestedTypeScanner = type.getNestedType("1NestedTypeScanner");
		final CtTypeReference<?> expectedNested = nestedTypeScanner.getReference();

		final CtTypeAccess<Foo> fooTypeAccess = factory.Code().createTypeAccess(expectedType);
		final CtThisAccess expectedThisAccess = factory.Code().createThisAccess(expectedType);
		final CtThisAccess expectedSuperThisAccess = factory.Code().createThisAccess(expectedSuperClassType);
		final CtThisAccess<?> expectedInnerClassAccess = factory.Code().createThisAccess(expectedInnerClass);
		final CtThisAccess expectedNestedAccess = factory.Code().createThisAccess(expectedNested);

		final CtMethod<?> innerInvMethod = innerClass.getMethodsByName("innerInv").get(0);
		final List<CtInvocation<?>> elements = innerInvMethod.getElements(new TypeFilter<>(CtInvocation.class));
		assertEquals(8, elements.size());
		expectedThisAccess.setType(expectedInnerClass);
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedType).target(expectedThisAccess).result("inv()"), elements.get(0));
		expectedThisAccess.setType(expectedType);
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedType).target(expectedThisAccess).result("this.inv()"), elements.get(1));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedType).target(fooTypeAccess).result("spoon.test.targeted.testclasses.Foo.staticMethod()"), elements.get(2));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedType).target(fooTypeAccess).result("spoon.test.targeted.testclasses.Foo.staticMethod()"), elements.get(3));
		expectedSuperThisAccess.setType(expectedInnerClass);
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedSuperClassType).target(expectedSuperThisAccess).result("superMethod()"), elements.get(4));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedSuperClassType).target(expectedThisAccess).result("this.superMethod()"), elements.get(5));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedInnerClass).target(expectedInnerClassAccess).result("method()"), elements.get(6));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedInnerClass).target(expectedInnerClassAccess).result("this.method()"), elements.get(7));

		final List<CtInvocation> newElements = nestedTypeScanner.getMethodsByName("checkType").get(0).getElements(new TypeFilter<>(CtInvocation.class));
		assertEquals(1, newElements.size());
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedNested).target(expectedNestedAccess).result("this.checkType(type)"), newElements.get(0));
	}

	@Test
	public void testTargetsOfInvInAnonymousClass() throws Exception {
		// contract: Specify declaring type of the executable of an invocation, the target of the invocation, and its result. All this in an anonymous class.
		final Factory factory = build(Foo.class, Bar.class, SuperClass.class);
		final CtClass<Foo> type = factory.Class().get(Foo.class);
		final CtTypeReference<Foo> expectedType = type.getReference();
		final CtClass<?> anonymousClass = type.getElements(new TypeFilter<CtClass>(CtClass.class) {
			@Override
			public boolean matches(CtClass element) {
				return element.isAnonymous() && super.matches(element);
			}
		}).get(0);
		final CtTypeReference<?> expectedAnonymousType = anonymousClass.getReference();
		final CtThisAccess<Foo> expectedThisAccess = factory.Code().createThisAccess(expectedType);
		final CtThisAccess expectedAnonymousThisAccess = factory.Code().createThisAccess(expectedAnonymousType);

		final CtMethod<?> method = anonymousClass.getMethodsByName("m").get(0);
		final List<CtInvocation> elements = method.getElements(new TypeFilter<>(CtInvocation.class));
		assertEquals(2, elements.size());
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedType).target(expectedThisAccess).result("this.invStatic()"), elements.get(0));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(expectedAnonymousType).target(expectedAnonymousThisAccess).result("this.invStatic()"), elements.get(1));
	}

	@Test
	public void testStaticTargetsOfInvNoClasspath() {
		// contract: Specify declaring type of the executable of an invocation, the target of the invocation and its result. All this in no classpath mode.
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/spoon/test/noclasspath/targeted/Foo.java");
		launcher.setSourceOutputDirectory("./target/noclasspath");
		launcher.run();

		final CtTypeReference<Object> foo = launcher.getFactory().Class().createReference("Foo");
		final CtTypeReference<Object> bar = launcher.getFactory().Class().createReference("Bar");
		final CtThisAccess<Object> expectedThisAccess = launcher.getFactory().Code().createThisAccess(foo);
		final CtTypeAccess<Object> expectedTypeAccess = launcher.getFactory().Code().createTypeAccess(foo);
		final CtTypeAccess<Object> expectedBarTypeAccess = launcher.getFactory().Code().createTypeAccess(bar);
		final CtTypeAccess<Object> fiiFuuTypeAccess = launcher.getFactory().Code().createTypeAccess(launcher.getFactory().Type().createReference("Fii.Fuu"));

		final CtMethod<?> invMethod = launcher.getFactory().Class().get("Foo").getMethodsByName("inv").get(0);
		final List<CtInvocation<?>> elements = invMethod.getElements(new TypeFilter<>(CtInvocation.class));
		assertEquals(8, elements.size());
		assertEqualsInvocation(new ExpectedTargetedExpression().target(CtConstructorCallImpl.class).result("new Foo(0, 0).staticMethod()"), elements.get(0));
		assertEqualsInvocation(new ExpectedTargetedExpression().target(CtFieldReadImpl.class).result("foo.staticMethod()"), elements.get(1));
		assertEqualsInvocation(new ExpectedTargetedExpression().target(expectedThisAccess).result("this.staticMethod()"), elements.get(2));
		assertEqualsInvocation(new ExpectedTargetedExpression().target(expectedTypeAccess).result("Foo.staticMethod()"), elements.get(3));
		assertEqualsInvocation(new ExpectedTargetedExpression().target(expectedThisAccess).result("staticMethod()"), elements.get(4));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(bar).target(expectedBarTypeAccess).result("Bar.staticMethodBar()"), elements.get(5));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(bar).target(expectedBarTypeAccess).result("Bar.staticMethodBar()"), elements.get(6));
		assertEqualsInvocation(new ExpectedTargetedExpression().declaringType(launcher.getFactory().Class().create("Fii.Fuu").getReference()).target(fiiFuuTypeAccess).result("Fii.Fuu.m()"), elements.get(7));
	}

	@Test
	public void testInitializeFieldAccessInNoclasspathMode() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/spoon/test/noclasspath/targeted/Foo.java");
		launcher.setSourceOutputDirectory("./target/noclasspath");
		launcher.run();

		final CtTypeReference<Object> expectedFoo = launcher.getFactory().Class().createReference("Foo");
		final CtThisAccess<Object> expectedThisAccess = launcher.getFactory().Code().createThisAccess(expectedFoo);

		final List<CtFieldAccess<?>> elements = launcher.getFactory().Class().get("Foo").getConstructor().getElements(new TypeFilter<>(CtFieldAccess.class));
		assertEquals(1, elements.size());
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(expectedFoo).target(expectedThisAccess).result("this.bar"), elements.get(0));
	}

	@Test
	public void testClassDeclaredInALambda() throws Exception {
		// contract: A class can be declared in a lambda expression where we use final fields.
		final CtType<Tapas> type = buildClass(Tapas.class);
		final List<CtFieldAccess> elements = new SortedList(new CtLineElementComparator());
		elements.addAll(type.getElements(new TypeFilter<>(CtFieldAccess.class)));
		assertEquals(3, elements.size());

		final CtTypeReference<Object> firstExpected = type.getFactory().Type().createReference("spoon.test.targeted.testclasses.Tapas$1$InnerSubscriber");
		CtThisAccess<Object> expectedThisAccess = type.getFactory().Code().createThisAccess(firstExpected);
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(firstExpected).target(expectedThisAccess).type(CtFieldWrite.class).result("this.index"), elements.get(0));

		final CtTypeReference<Object> secondExpectedInner = type.getFactory().Type().createReference("spoon.test.targeted.testclasses.Tapas$3InnerSubscriber");
		expectedThisAccess = type.getFactory().Code().createThisAccess(secondExpectedInner);
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(secondExpectedInner).target(expectedThisAccess).type(CtFieldWrite.class).result("this.index").isLocal(), elements.get(1));

		final CtTypeReference<Object> thirdExpectedInner = type.getFactory().Type().createReference("spoon.test.targeted.testclasses.Tapas$4InnerSubscriber");
		expectedThisAccess = type.getFactory().Code().createThisAccess(thirdExpectedInner);
		assertEqualsFieldAccess(new ExpectedTargetedExpression().declaringType(thirdExpectedInner).target(expectedThisAccess).type(CtFieldWrite.class).result("this.index").isLocal(), elements.get(2));
	}

	private void assertEqualsFieldAccess(ExpectedTargetedExpression expected, CtFieldAccess<?> fieldAccess) {
		if (expected.declaringType == null) {
			assertNull(fieldAccess.getVariable().getDeclaringType());
		} else {
			assertEquals(expected.isLocal, fieldAccess.getVariable().getDeclaringType().isLocalType());
			assertEquals(expected.declaringType.getQualifiedName(), fieldAccess.getVariable().getDeclaringType().getQualifiedName());
		}
		if (expected.targetClass != null) {
			assertSame(expected.targetClass, fieldAccess.getTarget().getClass());
		} else if (expected.targetString != null) {
			assertEquals(expected.targetString, fieldAccess.getTarget().toString());
		}
		assertEquals(expected.result, fieldAccess.toString());
		if (expected.type != null) {
			assertTrue(expected.type.isInstance(fieldAccess));
		}
	}


	private void assertEqualsInvocation(ExpectedTargetedExpression expected, CtInvocation<?> invocation) {
		// two required parts: toString and declaringType (type containing the method to be called)
		assertEquals(expected.result, invocation.toString());
		assertEquals(expected.declaringType, invocation.getExecutable().getDeclaringType());

		// + two optional parts
		if (expected.targetClass != null) {
			assertSame(expected.targetClass, invocation.getTarget().getClass());
		} else if (expected.targetString != null) {
			assertEquals(expected.targetString, invocation.getTarget().toString());
		}
	}

	private class ExpectedTargetedExpression {
		Class<? extends CtExpression> type;
		Class<? extends CtExpression> targetClass;
		String targetString;
		CtExpression<?> target;
		CtTypeReference<?> declaringType;
		String result;
		boolean isLocal = false;

		public ExpectedTargetedExpression type(Class<? extends CtExpression> type) {
			this.type = type;
			return this;
		}

		public ExpectedTargetedExpression target(Class<? extends CtExpression> target) {
			this.targetClass = target;
			return this;
		}

		public ExpectedTargetedExpression target(String target) {
			this.targetString = target;
			return this;
		}

		public ExpectedTargetedExpression target(CtExpression<?> target) {
			this.target = target;
			return this;
		}

		public ExpectedTargetedExpression declaringType(CtTypeReference<?> declaringType) {
			this.declaringType = declaringType;
			return this;
		}

		public ExpectedTargetedExpression result(String result) {
			this.result = result;
			return this;
		}

		public ExpectedTargetedExpression isLocal() {
			this.isLocal = true;
			return this;
		}
	}
}
