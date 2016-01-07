package spoon.test.targeted;

import org.junit.Test;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtTypeAccess;
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
import spoon.support.reflect.code.CtThisAccessImpl;
import spoon.test.targeted.testclasses.Bar;
import spoon.test.targeted.testclasses.Foo;
import spoon.test.targeted.testclasses.InternalSuperCall;

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
		Factory factory = build(Foo.class, Bar.class);
		final CtClass<Object> type = factory.Class().get(Foo.class);
		CtConstructor<?> constructor = type.getConstructors().toArray(new CtConstructor<?>[0])[0];

		final List<CtFieldAccess<?>> elements = constructor.getElements(new TypeFilter<CtFieldAccess<?>>(CtFieldAccess.class));
		assertEquals(2, elements.size());

		assertEquals("Target is CtThisAccessImpl if there is a 'this' explicit.", CtThisAccessImpl.class, elements.get(0).getTarget().getClass());
		assertNull("Targets is null if there isn't a 'this' explicit.", elements.get(1).getTarget());
	}

	@Test
	public void testNotTargetedExpression() throws Exception {
		Factory factory = build(Foo.class, Bar.class);
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
	public void testStaticTargets() throws Exception {
		final Factory factory = build(Foo.class, Bar.class);
		final CtClass<Foo> type = factory.Class().get(Foo.class);
		final CtTypeReference<Foo> expectedType = type.getReference();
		final CtTypeReference<Bar> expectedBarType = factory.Class().<Bar>get(Bar.class).getReference();
		final CtMethod<?> constructor = type.getMethodsByName("m").get(0);

		final List<CtFieldAccess<?>> elements = constructor.getElements(new TypeFilter<CtFieldAccess<?>>(CtFieldAccess.class));
		assertEquals(10, elements.size());

		assertTrue(elements.get(0) instanceof CtFieldRead);
		assertTrue(elements.get(1) instanceof CtFieldRead);
		assertTrue(elements.get(2) instanceof CtFieldRead);

		assertTrue(elements.get(3) instanceof CtFieldWrite);
		assertTrue(elements.get(4) instanceof CtFieldWrite);
		assertTrue(elements.get(5) instanceof CtFieldWrite);

		assertTrue(elements.get(6) instanceof CtFieldRead);
		assertTrue(elements.get(7) instanceof CtFieldRead);

		assertTrue(elements.get(8) instanceof CtFieldWrite);
		assertTrue(elements.get(9) instanceof CtFieldWrite);

		// contract for static calls, declaring type of all variables are the same.
		assertEquals(expectedType, elements.get(0).getVariable().getDeclaringType());
		assertEquals(expectedType, elements.get(1).getVariable().getDeclaringType());
		assertEquals(expectedType, elements.get(2).getVariable().getDeclaringType());
		assertEquals(expectedType, elements.get(3).getVariable().getDeclaringType());
		assertEquals(expectedType, elements.get(4).getVariable().getDeclaringType());
		assertEquals(expectedType, elements.get(5).getVariable().getDeclaringType());

		assertEquals(expectedBarType, elements.get(6).getVariable().getDeclaringType());
		assertEquals(expectedBarType, elements.get(7).getVariable().getDeclaringType());
		assertEquals(expectedBarType, elements.get(8).getVariable().getDeclaringType());
		assertEquals(expectedBarType, elements.get(9).getVariable().getDeclaringType());

		// contract for static calls getTarget() have a this or type access.
		final CtThisAccess<Foo> exepectedThisAccess = type.getFactory().Core().createThisAccess();
		exepectedThisAccess.setType(expectedType);
		final CtTypeAccess<Foo> expectedTypeAccess = type.getFactory().Code().createTypeAccess(expectedType);
		final CtTypeAccess<Bar> expectedBarTypeAccess = type.getFactory().Code().createTypeAccess(expectedBarType);

		assertEquals(exepectedThisAccess, elements.get(0).getTarget());
		assertEquals(expectedTypeAccess, elements.get(1).getTarget());
		assertEquals(expectedTypeAccess, elements.get(2).getTarget());
		assertEquals(exepectedThisAccess, elements.get(3).getTarget());
		assertEquals(expectedTypeAccess, elements.get(4).getTarget());
		assertEquals(expectedTypeAccess, elements.get(5).getTarget());
		assertEquals(expectedBarTypeAccess, elements.get(6).getTarget());
		assertEquals(expectedBarTypeAccess, elements.get(7).getTarget());
		assertEquals(expectedBarTypeAccess, elements.get(8).getTarget());
		assertEquals(expectedBarTypeAccess, elements.get(9).getTarget());
	}
}
