package spoon.test.targeted;

import org.junit.Test;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtThisAccessImpl;
import spoon.test.targeted.testclasses.Foo;
import spoon.test.targeted.testclasses.InternalSuperCall;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static spoon.test.TestUtils.build;

public class TargetedExpressionTest {
	@Test
	public void testCtSuperAccess() throws Exception {
		final Factory factory = build(InternalSuperCall.class);
		final CtClass<?> ctClass = factory.Class().get(InternalSuperCall.class);
		final List<CtSuperAccess> superAccesses = ctClass.getElements(new AbstractFilter<CtSuperAccess>(CtSuperAccess.class) {
			@Override
			public boolean matches(CtSuperAccess element) {
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
		CtClass<?> type = build("spoon.test.targeted.testclasses", "Foo");
		CtConstructor<?> constructor = type.getConstructors().toArray(new CtConstructor<?>[0])[0];

		final List<CtFieldAccess<?>> elements = constructor.getElements(new TypeFilter<CtFieldAccess<?>>(CtFieldAccess.class));
		assertEquals(2, elements.size());

		assertEquals("Target is CtThisAccessImpl if there is a 'this' explicit.", CtThisAccessImpl.class, elements.get(0).getTarget().getClass());
		assertNull("Targets is null if there isn't a 'this' explicit.", elements.get(1).getTarget());
	}

	@Test
	public void testNotTargetedExpression() throws Exception {
		Factory factory = build(Foo.class);
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
}
