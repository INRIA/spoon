package spoon.test.targeted;

import com.sun.org.apache.bcel.internal.classfile.InnerClass;
import org.junit.Test;
import spoon.Launcher;
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
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtConstructorCallImpl;
import spoon.support.reflect.code.CtFieldReadImpl;
import spoon.support.reflect.code.CtThisAccessImpl;
import spoon.test.TestUtils;
import spoon.test.targeted.testclasses.Bar;
import spoon.test.targeted.testclasses.Foo;
import spoon.test.targeted.testclasses.InternalSuperCall;
import spoon.test.targeted.testclasses.Pozole;
import spoon.test.targeted.testclasses.SuperClass;
import spoon.test.targeted.testclasses.Tapas;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static spoon.test.TestUtils.build;
import static spoon.test.TestUtils.buildClass;

public class TargetedExpressionTest {
	@Test
	public void testCtSuperAccess() throws Exception {
		final Factory factory = build(InternalSuperCall.class);
		final CtClass<?> ctClass = factory.Class().get(InternalSuperCall.class);
		final List<CtSuperAccess<?>> superAccesses = ctClass.getElements(new AbstractFilter<CtSuperAccess<?>>(CtSuperAccess.class) {
			@Override
			public boolean matches(CtSuperAccess<?> element) {
				return super.matches(element);
			}
		});
		assertEquals(2, superAccesses.size());
		assertNull(superAccesses.get(0).getTarget());
		assertNotNull(superAccesses.get(1).getTarget());

		CtMethod<?> method = ctClass.getElements(new NameFilter<CtMethod<?>>("methode")).get(0);
		assertEquals(
				"spoon.test.targeted.testclasses.InternalSuperCall.super.toString()",
				method.getBody().getStatements().get(0).toString());

		CtMethod<?> toString = ctClass.getElements(new NameFilter<CtMethod<?>>("toString")).get(0);
		assertEquals(
				"return super.toString()",
				toString.getBody().getStatements().get(0).toString());
	}

	@Test
	public void testCtThisAccess() throws Exception {
		CtType<?> type = build("spoon.test.targeted.testclasses", "InnerClassThisAccess");
		assertEquals("InnerClassThisAccess", type.getSimpleName());

		CtMethod<?> meth1 = type.getElements(new NameFilter<CtMethod<?>>("method2")).get(0);
		assertEquals(
				"spoon.test.targeted.testclasses.InnerClassThisAccess.this.method()",
				meth1.getBody().getStatements().get(0).toString());

		CtClass<?> c = type.getElements(new NameFilter<CtClass<?>>("InnerClass")).get(0);
		assertEquals("InnerClass", c.getSimpleName());
		CtConstructor<?> ctr = c.getConstructor(type.getFactory().Type().createReference(boolean.class));
		assertEquals("this.b = b", ctr.getBody().getLastStatement().toString());
	}

	@Test
	public void testTargetOfFieldAccess() throws Exception {
		Factory factory = build(Foo.class, Bar.class, SuperClass.class);
		final CtClass<Object> type = factory.Class().get(Foo.class);
		CtConstructor<?> constructor = type.getConstructors().toArray(new CtConstructor<?>[0])[0];

		final List<CtFieldAccess<?>> elements = constructor.getElements(new TypeFilter<CtFieldAccess<?>>(CtFieldAccess.class));
		assertEquals(2, elements.size());

		assertEquals("Target is CtThisAccessImpl if there is a 'this' explicit.", CtThisAccessImpl.class, elements.get(0).getTarget().getClass());
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
		final Factory factory = TestUtils.build(Pozole.class);
		final CtClass<Object> aPozole = factory.Class().get(Pozole.class);
		final CtConstructor<Object> aConstructor = aPozole.getConstructor(aPozole.getReference());

		final List<CtFieldRead> elements = aConstructor.getElements(new TypeFilter<>(CtFieldRead.class));
		assertEquals(1, elements.size());
		assertEquals("((spoon.test.targeted.testclasses.Pozole<T>)(v1))", elements.get(0).getTarget().toString());
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

		final CtThisAccess<Foo> expectedThisAccess = type.getFactory().Core().createThisAccess();
		expectedThisAccess.setType(expectedType);

		final List<CtFieldAccess<?>> elements = fieldMethod.getElements(new TypeFilter<CtFieldAccess<?>>(CtFieldAccess.class));
		assertEquals(10, elements.size());
		assertFieldAccess(new Expected().declaringType(expectedType).target(expectedThisAccess).result("spoon.test.targeted.testclasses.Foo.this.i"), elements.get(0));
		assertFieldAccess(new Expected().declaringType(expectedType).target(expectedThisAccess).result("i"), elements.get(1));
		assertFieldAccess(new Expected().declaringType(expectedBarType).target(elements.get(3)).result("spoon.test.targeted.testclasses.Foo.this.bar.i"), elements.get(2));
		assertFieldAccess(new Expected().declaringType(expectedType).target(expectedThisAccess).result("spoon.test.targeted.testclasses.Foo.this.bar"), elements.get(3));
		assertFieldAccess(new Expected().declaringType(expectedBarType).target(elements.get(5)).result("bar.i"), elements.get(4));
		assertFieldAccess(new Expected().declaringType(expectedType).target(expectedThisAccess).result("bar"), elements.get(5));
		assertFieldAccess(new Expected().declaringType(expectedSuperClassType).target(expectedThisAccess).result("spoon.test.targeted.testclasses.Foo.this.o"), elements.get(6));
		assertFieldAccess(new Expected().declaringType(expectedSuperClassType).target(expectedThisAccess).result("o"), elements.get(7));
		assertFieldAccess(new Expected().declaringType(expectedFuuType).target(elements.get(9)).result("fuu.p"), elements.get(8));
		assertFieldAccess(new Expected().declaringType(expectedType).target(expectedThisAccess).result("fuu"), elements.get(9));
	}

	@Test
	public void testTargetsOfStaticFieldAccess() throws Exception {
		final Factory factory = build(Foo.class, Bar.class, SuperClass.class);
		final CtClass<Foo> type = factory.Class().get(Foo.class);
		final CtTypeReference<Foo> expectedType = type.getReference();
		final CtTypeReference<Bar> expectedBarType = factory.Class().<Bar>get(Bar.class).getReference();
		final CtMethod<?> constructor = type.getMethodsByName("m").get(0);

		final CtThisAccess<Foo> exepectedThisAccess = type.getFactory().Core().createThisAccess();
		exepectedThisAccess.setType(expectedType);
		final CtTypeAccess<Foo> expectedTypeAccess = type.getFactory().Code().createTypeAccess(expectedType);
		final CtTypeAccess<Bar> expectedBarTypeAccess = type.getFactory().Code().createTypeAccess(expectedBarType);

		final List<CtFieldAccess<?>> elements = constructor.getElements(new TypeFilter<CtFieldAccess<?>>(CtFieldAccess.class));
		assertEquals(10, elements.size());
		assertFieldAccess(new Expected().type(CtFieldRead.class).declaringType(expectedType).target(exepectedThisAccess).result("spoon.test.targeted.testclasses.Foo.this.k"), elements.get(0));
		assertFieldAccess(new Expected().type(CtFieldRead.class).declaringType(expectedType).target(expectedTypeAccess).result("spoon.test.targeted.testclasses.Foo.k"), elements.get(1));
		assertFieldAccess(new Expected().type(CtFieldRead.class).declaringType(expectedType).target(expectedTypeAccess).result("spoon.test.targeted.testclasses.Foo.k"), elements.get(2));
		assertFieldAccess(new Expected().type(CtFieldWrite.class).declaringType(expectedType).target(exepectedThisAccess).result("spoon.test.targeted.testclasses.Foo.this.k"), elements.get(3));
		assertFieldAccess(new Expected().type(CtFieldWrite.class).declaringType(expectedType).target(expectedTypeAccess).result("spoon.test.targeted.testclasses.Foo.k"), elements.get(4));
		assertFieldAccess(new Expected().type(CtFieldWrite.class).declaringType(expectedType).target(expectedTypeAccess).result("spoon.test.targeted.testclasses.Foo.k"), elements.get(5));
		assertFieldAccess(new Expected().type(CtFieldRead.class).declaringType(expectedBarType).target(expectedBarTypeAccess).result("spoon.test.targeted.testclasses.Bar.FIELD"), elements.get(6));
		assertFieldAccess(new Expected().type(CtFieldRead.class).declaringType(expectedBarType).target(expectedBarTypeAccess).result("spoon.test.targeted.testclasses.Bar.FIELD"), elements.get(7));
		assertFieldAccess(new Expected().type(CtFieldWrite.class).declaringType(expectedBarType).target(expectedBarTypeAccess).result("spoon.test.targeted.testclasses.Bar.FIELD"), elements.get(8));
		assertFieldAccess(new Expected().type(CtFieldWrite.class).declaringType(expectedBarType).target(expectedBarTypeAccess).result("spoon.test.targeted.testclasses.Bar.FIELD"), elements.get(9));

		final CtAnonymousExecutable staticInit = type.getAnonymousExecutables().get(0);
		final List<CtFieldAccess<?>> staticElements = staticInit.getElements(new TypeFilter<>(CtFieldAccess.class));
		assertEquals(1, staticElements.size());
		assertFieldAccess(new Expected().type(CtFieldWrite.class).declaringType(expectedType).target(expectedTypeAccess).result("p"), staticElements.get(0));
	}

	@Test
	public void testTargetsOfFieldAccessInInnerClass() throws Exception {
		final Factory factory = build(Foo.class, Bar.class, SuperClass.class);
		final CtClass<Foo> type = factory.Class().get(Foo.class);
		final CtTypeReference<Foo> expectedType = type.getReference();
		final CtTypeReference<SuperClass> expectedSuperClassType = factory.Class().<SuperClass>get(SuperClass.class).getReference();
		final CtType<InnerClass> innerClass = type.getNestedType("InnerClass");
		final CtTypeReference<InnerClass> expectedInnerClass = innerClass.getReference();
		final CtType<?> nestedTypeScanner = type.getNestedType("NestedTypeScanner");
		final CtTypeReference<?> expectedNested = nestedTypeScanner.getReference();
		expectedNested.<CtTypeReference>setDeclaringType(null);

		final CtTypeAccess<Foo> fooTypeAccess = factory.Code().createTypeAccess(expectedType);
		final CtThisAccess<Foo> expectedThisAccess = factory.Core().createThisAccess();
		expectedThisAccess.setType(expectedType);
		expectedThisAccess.setImplicit(true);
		final CtThisAccess<SuperClass> expectedSuperThisAccess = factory.Core().createThisAccess();
		expectedSuperThisAccess.setType(expectedSuperClassType);
		expectedSuperThisAccess.setImplicit(true);
		final CtThisAccess<InnerClass> expectedInnerClassAccess = factory.Core().createThisAccess();
		expectedInnerClassAccess.setType(expectedInnerClass);
		expectedInnerClassAccess.setImplicit(true);
		final CtThisAccess expectedNestedAccess = factory.Core().createThisAccess();
		expectedNestedAccess.setType(expectedNested);

		final CtMethod<?> innerInvMethod = innerClass.getMethodsByName("innerField").get(0);
		final List<CtFieldAccess<?>> elements = innerInvMethod.getElements(new TypeFilter<>(CtFieldAccess.class));
		assertEquals(6, elements.size());
		assertFieldAccess(new Expected().declaringType(expectedInnerClass).target(expectedInnerClassAccess).result("spoon.test.targeted.testclasses.Foo.InnerClass.this.i"), elements.get(0));
		assertFieldAccess(new Expected().declaringType(expectedInnerClass).target(expectedInnerClassAccess).result("i"), elements.get(1));
		assertFieldAccess(new Expected().declaringType(expectedType).target(expectedThisAccess).result("spoon.test.targeted.testclasses.Foo.this.i"), elements.get(2));
		assertFieldAccess(new Expected().declaringType(expectedType).target(fooTypeAccess).result("spoon.test.targeted.testclasses.Foo.k"), elements.get(3));
		assertFieldAccess(new Expected().declaringType(expectedSuperClassType).target(expectedThisAccess).result("spoon.test.targeted.testclasses.Foo.this.o"), elements.get(4));
		assertFieldAccess(new Expected().declaringType(expectedSuperClassType).target(expectedThisAccess).result("o"), elements.get(5));

		final List<CtFieldAccess<?>> newElements = nestedTypeScanner.getMethodsByName("checkField").get(0).getElements(new TypeFilter<>(CtFieldAccess.class));
		assertEquals(2, newElements.size());
		assertFieldAccess(new Expected().declaringType(expectedNested).target(expectedNestedAccess).result("NestedTypeScanner.this.type"), newElements.get(0));
		assertFieldAccess(new Expected().declaringType(expectedNested).target(expectedNestedAccess).result("type"), newElements.get(1));
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
		final CtThisAccess<Foo> expectedThisAccess = factory.Core().createThisAccess();
		expectedThisAccess.setType(expectedType);
		final CtThisAccess expectedAnonymousThisAccess = factory.Core().createThisAccess();
		expectedAnonymousThisAccess.setType(expectedAnonymousType);

		final CtMethod<?> method = anonymousClass.getMethodsByName("invStatic").get(0);
		final List<CtFieldAccess> elements = method.getElements(new TypeFilter<>(CtFieldAccess.class));
		assertEquals(3, elements.size());
		assertFieldAccess(new Expected().declaringType(expectedType).target(expectedThisAccess).result("spoon.test.targeted.testclasses.Foo.this.i"), elements.get(0));
		assertFieldAccess(new Expected().declaringType(expectedAnonymousType).target(expectedAnonymousThisAccess).result("this.i"), elements.get(1));
		assertFieldAccess(new Expected().declaringType(expectedAnonymousType).target(expectedAnonymousThisAccess).result("i"), elements.get(2));
	}

	@Test
	public void testStaticTargetsOfFieldAccessNoClasspath() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/spoon/test/noclasspath/targeted/Foo.java");
		launcher.setSourceOutputDirectory("./target/noclasspath");
		launcher.run();

		final CtClass<Object> foo = launcher.getFactory().Class().get("Foo");
		final CtTypeReference<Object> expectedFoo = foo.getReference();
		final CtTypeReference<Object> bar = launcher.getFactory().Class().create("Bar").getReference();
		final CtTypeReference<Object> fiiFuu = launcher.getFactory().Class().create("Fii.Fuu").getReference();
		final CtThisAccess<Object> exepectedThisAccess = launcher.getFactory().Core().createThisAccess();
		exepectedThisAccess.setType(expectedFoo);
		final CtTypeAccess<Object> expectedTypeAccess = launcher.getFactory().Code().createTypeAccess(foo.getReference());
		final CtTypeAccess<Object> expectedBarTypeAccess = launcher.getFactory().Code().createTypeAccess(bar);

		final CtMethod<?> fieldMethod = foo.getMethodsByName("field").get(0);
		final List<CtFieldAccess<?>> elements = fieldMethod.getElements(new TypeFilter<>(CtFieldAccess.class));
		assertEquals(10, elements.size());
		assertFieldAccess(new Expected().declaringType(expectedFoo).target(CtConstructorCallImpl.class).result("new Foo().i"), elements.get(0));
		assertFieldAccess(new Expected().declaringType(expectedFoo).target(elements.get(2)).result("foo.i"), elements.get(1));
		assertFieldAccess(new Expected().declaringType(expectedFoo).target(exepectedThisAccess).result("foo"), elements.get(2));
		assertFieldAccess(new Expected().declaringType(expectedFoo).target(exepectedThisAccess).result("Foo.this.i"), elements.get(3));
		assertFieldAccess(new Expected().declaringType(expectedFoo).target(exepectedThisAccess).result("foo"), elements.get(4));
		assertFieldAccess(new Expected().declaringType(expectedFoo).target(expectedTypeAccess).result("Foo.staticField"), elements.get(5));
		assertFieldAccess(new Expected().result("staticField"), elements.get(6));
		assertFieldAccess(new Expected().declaringType(bar).target(expectedBarTypeAccess).result("Bar.staticFieldBar"), elements.get(7));
		assertFieldAccess(new Expected().declaringType(bar).target(expectedBarTypeAccess).result("Bar.staticFieldBar"), elements.get(8));
		assertFieldAccess(new Expected().declaringType(fiiFuu).target(launcher.getFactory().Code().createTypeAccess(fiiFuu)).result("Fii.Fuu.i"), elements.get(9));
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
		final CtMethod<?> invMethod = type.getMethodsByName("inv").get(0);

		final CtThisAccess<Foo> expectedThisAccess = factory.Core().createThisAccess();
		expectedThisAccess.setType(expectedType);
		final CtTypeAccess<Foo> fooTypeAccess = factory.Code().createTypeAccess(expectedType);
		final CtTypeAccess<SuperClass> superClassTypeAccess = factory.Code().createTypeAccess(expectedSuperClassType);

		final List<CtInvocation<?>> elements = invMethod.getElements(new TypeFilter<CtInvocation<?>>(CtInvocation.class));
		assertEquals(7, elements.size());

		assertInvocation(new Expected().declaringType(expectedType).target(CtConstructorCallImpl.class).result("new spoon.test.targeted.testclasses.Foo(0 , 0).method()"), elements.get(0));
		assertInvocation(new Expected().declaringType(expectedType).target(CtFieldReadImpl.class).result("foo.method()"), elements.get(1));
		assertInvocation(new Expected().declaringType(expectedType).target(expectedThisAccess).result("spoon.test.targeted.testclasses.Foo.this.method()"), elements.get(2));
		assertInvocation(new Expected().declaringType(expectedType).target(expectedThisAccess).result("method()"), elements.get(3));
		assertInvocation(new Expected().declaringType(expectedBarType).target(CtFieldReadImpl.class).result("bar.methodBar()"), elements.get(4));
		assertInvocation(new Expected().declaringType(expectedFuuType).target(CtFieldReadImpl.class).result("fuu.method()"), elements.get(5));
		assertInvocation(new Expected().declaringType(expectedSuperClassType).target(expectedThisAccess).result("superMethod()"), elements.get(6));

		assertEquals(fooTypeAccess, ((CtThisAccess) elements.get(2).getTarget()).getTarget());
		assertEquals(fooTypeAccess, ((CtThisAccess) elements.get(3).getTarget()).getTarget());
		assertEquals(superClassTypeAccess, ((CtThisAccess) elements.get(6).getTarget()).getTarget());
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

		final CtThisAccess<Foo> exepectedThisAccess = type.getFactory().Core().createThisAccess();
		exepectedThisAccess.setType(expectedType);
		final CtTypeAccess<Foo> expectedTypeAccess = type.getFactory().Code().createTypeAccess(expectedType);
		final CtTypeAccess<Bar> expectedBarTypeAccess = type.getFactory().Code().createTypeAccess(expectedBarType);

		final CtMethod<?> invMethod = type.getMethodsByName("invStatic").get(0);
		final List<CtInvocation<?>> elements = invMethod.getElements(new TypeFilter<CtInvocation<?>>(CtInvocation.class));
		assertEquals(8, elements.size());
		assertInvocation(new Expected().declaringType(expectedType).target(CtConstructorCallImpl.class).result("new spoon.test.targeted.testclasses.Foo(0 , 0).staticMethod()"), elements.get(0));
		assertInvocation(new Expected().declaringType(expectedType).target(CtFieldReadImpl.class).result("foo.staticMethod()"), elements.get(1));
		assertInvocation(new Expected().declaringType(expectedType).target(exepectedThisAccess).result("spoon.test.targeted.testclasses.Foo.this.staticMethod()"), elements.get(2));
		assertInvocation(new Expected().declaringType(expectedType).target(expectedTypeAccess).result("spoon.test.targeted.testclasses.Foo.staticMethod()"), elements.get(3));
		assertInvocation(new Expected().declaringType(expectedType).target(expectedTypeAccess).result("spoon.test.targeted.testclasses.Foo.staticMethod()"), elements.get(4));
		assertInvocation(new Expected().declaringType(expectedBarType).target(expectedBarTypeAccess).result("spoon.test.targeted.testclasses.Bar.staticMethodBar()"), elements.get(5));
		assertInvocation(new Expected().declaringType(expectedBarType).target(expectedBarTypeAccess).result("spoon.test.targeted.testclasses.Bar.staticMethodBar()"), elements.get(6));
		assertInvocation(new Expected().declaringType(expectedFuuType).target(factory.Code().createTypeAccess(expectedFuuType)).result("spoon.test.targeted.testclasses.Foo.Fii.Fuu.m()"), elements.get(7));
	}

	@Test
	public void testTargetsOfInvInInnerClass() throws Exception {
		// contract: Specify declaring type of the executable of an invocation, the target of the invocation and its result. All this in an innerclass.
		final Factory factory = build(Foo.class, Bar.class, SuperClass.class);
		final CtClass<Foo> type = factory.Class().get(Foo.class);
		final CtTypeReference<Foo> expectedType = type.getReference();
		final CtTypeReference<SuperClass> expectedSuperClassType = factory.Class().<SuperClass>get(SuperClass.class).getReference();
		final CtType<InnerClass> innerClass = type.getNestedType("InnerClass");
		final CtTypeReference<InnerClass> expectedInnerClass = innerClass.getReference();
		final CtType<?> nestedTypeScanner = type.getNestedType("NestedTypeScanner");
		final CtTypeReference<?> expectedNested = nestedTypeScanner.getReference();
		expectedNested.<CtTypeReference>setDeclaringType(null);

		final CtTypeAccess<Foo> fooTypeAccess = factory.Code().createTypeAccess(expectedType);
		final CtThisAccess<Foo> expectedThisAccess = factory.Core().createThisAccess();
		expectedThisAccess.setType(expectedType);
		expectedThisAccess.setImplicit(true);
		final CtThisAccess<SuperClass> expectedSuperThisAccess = factory.Core().createThisAccess();
		expectedSuperThisAccess.setType(expectedSuperClassType);
		expectedSuperThisAccess.setImplicit(true);
		final CtThisAccess<InnerClass> expectedInnerClassAccess = factory.Core().createThisAccess();
		expectedInnerClassAccess.setType(expectedInnerClass);
		expectedInnerClassAccess.setImplicit(true);
		final CtThisAccess expectedNestedAccess = factory.Core().createThisAccess();
		expectedNestedAccess.setType(expectedNested);

		final CtMethod<?> innerInvMethod = innerClass.getMethodsByName("innerInv").get(0);
		final List<CtInvocation<?>> elements = innerInvMethod.getElements(new TypeFilter<CtInvocation<?>>(CtInvocation.class));
		assertEquals(8, elements.size());
		assertInvocation(new Expected().declaringType(expectedType).target(expectedThisAccess.toString()).result("inv()"), elements.get(0));
		assertInvocation(new Expected().declaringType(expectedType).target(expectedThisAccess).result("spoon.test.targeted.testclasses.Foo.this.inv()"), elements.get(1));
		assertInvocation(new Expected().declaringType(expectedType).target(fooTypeAccess).result("spoon.test.targeted.testclasses.Foo.staticMethod()"), elements.get(2));
		assertInvocation(new Expected().declaringType(expectedType).target(fooTypeAccess).result("spoon.test.targeted.testclasses.Foo.staticMethod()"), elements.get(3));
		assertInvocation(new Expected().declaringType(expectedSuperClassType).target(expectedSuperThisAccess.toString()).result("superMethod()"), elements.get(4));
		assertInvocation(new Expected().declaringType(expectedSuperClassType).target(expectedThisAccess).result("spoon.test.targeted.testclasses.Foo.this.superMethod()"), elements.get(5));
		assertInvocation(new Expected().declaringType(expectedInnerClass).target(expectedInnerClassAccess).result("method()"), elements.get(6));
		assertInvocation(new Expected().declaringType(expectedInnerClass).target(expectedInnerClassAccess).result("spoon.test.targeted.testclasses.Foo.InnerClass.this.method()"), elements.get(7));

		final List<CtInvocation> newElements = nestedTypeScanner.getMethodsByName("checkType").get(0).getElements(new TypeFilter<>(CtInvocation.class));
		assertEquals(1, newElements.size());
		assertInvocation(new Expected().declaringType(expectedNested).target(expectedNestedAccess).result("NestedTypeScanner.this.checkType(type)"), newElements.get(0));
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
		final CtThisAccess<Foo> expectedThisAccess = factory.Core().createThisAccess();
		expectedThisAccess.setType(expectedType);
		final CtThisAccess expectedAnonymousThisAccess = factory.Core().createThisAccess();
		expectedAnonymousThisAccess.setType(expectedAnonymousType);

		final CtMethod<?> method = anonymousClass.getMethodsByName("m").get(0);
		final List<CtInvocation> elements = method.getElements(new TypeFilter<>(CtInvocation.class));
		assertEquals(2, elements.size());
		assertInvocation(new Expected().declaringType(expectedType).target(expectedThisAccess).result("spoon.test.targeted.testclasses.Foo.this.invStatic()"), elements.get(0));
		assertInvocation(new Expected().declaringType(expectedAnonymousType).target(expectedAnonymousThisAccess).result("this.invStatic()"), elements.get(1));
	}

	@Test
	public void testStaticTargetsOfInvNoClasspath() throws Exception {
		// contract: Specify declaring type of the executable of an invocation, the target of the invocation and its result. All this in no classpath mode.
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/spoon/test/noclasspath/targeted/Foo.java");
		launcher.setSourceOutputDirectory("./target/noclasspath");
		launcher.run();

		final CtClass<Object> foo = launcher.getFactory().Class().get("Foo");
		final CtTypeReference<Object> bar = launcher.getFactory().Class().create("Bar").getReference();
		final CtTypeReference<Object> fiiFuu = launcher.getFactory().Class().create("Fii.Fuu").getReference();
		final CtThisAccess<Object> exepectedThisAccess = launcher.getFactory().Core().createThisAccess();
		exepectedThisAccess.setType(foo.getReference());
		final CtTypeAccess<Object> expectedTypeAccess = launcher.getFactory().Code().createTypeAccess(foo.getReference());
		final CtTypeAccess<Object> expectedBarTypeAccess = launcher.getFactory().Code().createTypeAccess(bar);

		final CtMethod<?> invMethod = foo.getMethodsByName("inv").get(0);
		final List<CtInvocation<?>> elements = invMethod.getElements(new TypeFilter<>(CtInvocation.class));
		assertEquals(8, elements.size());
		assertInvocation(new Expected().target(CtConstructorCallImpl.class).result("new Foo(0 , 0).staticMethod()"), elements.get(0));
		assertInvocation(new Expected().target(CtFieldReadImpl.class).result("foo.staticMethod()"), elements.get(1));
		assertInvocation(new Expected().target(exepectedThisAccess).result("Foo.this.staticMethod()"), elements.get(2));
		assertInvocation(new Expected().target(expectedTypeAccess).result("Foo.staticMethod()"), elements.get(3));
		assertInvocation(new Expected().result("staticMethod()"), elements.get(4));
		assertInvocation(new Expected().declaringType(bar).target(expectedBarTypeAccess).result("Bar.staticMethodBar()"), elements.get(5));
		assertInvocation(new Expected().declaringType(bar).target(expectedBarTypeAccess).result("Bar.staticMethodBar()"), elements.get(6));
		assertInvocation(new Expected().declaringType(fiiFuu).target(launcher.getFactory().Code().createTypeAccess(fiiFuu)).result("Fii.Fuu.m()"), elements.get(7));
	}

	@Test
	public void testInitializeFieldAccessInNoclasspathMode() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/spoon/test/noclasspath/targeted/Foo.java");
		launcher.setSourceOutputDirectory("./target/noclasspath");
		launcher.run();

		final CtClass<Object> foo = launcher.getFactory().Class().get("Foo");
		final CtTypeReference<Object> expectedFoo = foo.getReference();
		final CtThisAccess<Object> exepectedThisAccess = launcher.getFactory().Core().createThisAccess();
		exepectedThisAccess.setType(expectedFoo);

		final CtConstructor<Object> constructor = foo.getConstructor();
		final List<CtFieldAccess<?>> elements = constructor.getElements(new TypeFilter<>(CtFieldAccess.class));
		assertEquals(1, elements.size());
		assertFieldAccess(new Expected().declaringType(expectedFoo).target(exepectedThisAccess).result("this.bar"), elements.get(0));
	}

	@Test
	public void testClassDeclaredInALambda() throws Exception {
		// contract: A class can be declared in a lambda expression where we use final fields.
		final CtType<Tapas> type = buildClass(Tapas.class);
		final CtTypeReference<Object> expectedInner = type.getFactory().Type().createReference("InnerSubscriber");

		final CtThisAccess<Object> exepectedThisAccess = type.getFactory().Core().createThisAccess();
		exepectedThisAccess.setType(expectedInner);

		final List<CtFieldAccess<?>> elements = type.getElements(new TypeFilter<>(CtFieldAccess.class));
		assertEquals(3, elements.size());
		assertFieldAccess(new Expected().declaringType(expectedInner).target(exepectedThisAccess).type(CtFieldWrite.class).result("InnerSubscriber.this.index"), elements.get(0));
		assertFieldAccess(new Expected().declaringType(expectedInner).target(exepectedThisAccess).type(CtFieldWrite.class).result("InnerSubscriber.this.index"), elements.get(1));
		assertFieldAccess(new Expected().declaringType(expectedInner).target(exepectedThisAccess).type(CtFieldWrite.class).result("this.index"), elements.get(2));
	}

	private void assertFieldAccess(Expected expected, CtFieldAccess<?> fieldAccess) {
		assertEquals(expected.declaringType, fieldAccess.getVariable().getDeclaringType());
		if (expected.targetClass != null) {
			assertEquals(expected.targetClass, fieldAccess.getTarget().getClass());
		} else if (expected.targetString != null) {
			assertEquals(expected.targetString, fieldAccess.getTarget().toString());
		} else {
			assertEquals(expected.target, fieldAccess.getTarget());
		}
		assertEquals(expected.result, fieldAccess.toString());
		if (expected.type != null) {
			assertTrue(expected.type.isInstance(fieldAccess));
		}
	}

	private void assertInvocation(Expected expected, CtInvocation<?> invocation) {
		assertEquals(expected.declaringType, invocation.getExecutable().getDeclaringType());
		if (expected.targetClass != null) {
			assertEquals(expected.targetClass, invocation.getTarget().getClass());
		} else if (expected.targetString != null) {
			assertEquals(expected.targetString, invocation.getTarget().toString());
		} else {
			assertEquals(expected.target, invocation.getTarget());
		}
		assertEquals(expected.result, invocation.toString());
	}

	private class Expected {
		Class<? extends CtExpression> type;
		Class<? extends CtExpression> targetClass;
		String targetString;
		CtExpression<?> target;
		CtTypeReference<?> declaringType;
		String result;

		public Expected type(Class<? extends CtExpression> type) {
			this.type = type;
			return this;
		}

		public Expected target(Class<? extends CtExpression> target) {
			this.targetClass = target;
			return this;
		}

		public Expected target(String target) {
			this.targetString = target;
			return this;
		}

		public Expected target(CtExpression<?> target) {
			this.target = target;
			return this;
		}

		public Expected declaringType(CtTypeReference<?> declaringType) {
			this.declaringType = declaringType;
			return this;
		}

		public Expected result(String result) {
			this.result = result;
			return this;
		}
	}
}
