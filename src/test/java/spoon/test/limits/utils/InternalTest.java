package spoon.test.limits.utils;

import org.junit.Test;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

public class InternalTest {

	@Test
	public void testInternalClasses() throws Exception {
		CtClass<?> type = build("spoon.test.limits.utils",
				"ContainInternalClass");
		assertEquals("ContainInternalClass", type.getSimpleName());
		List<CtClass<?>> classes = type.getElements(new TypeFilter<CtClass<?>>(
				CtClass.class));
		assertEquals(4, classes.size());
		CtClass<?> c1 = classes.get(1);
		assertEquals("InternalClass", c1.getSimpleName());
		assertEquals(
				"spoon.test.limits.utils.ContainInternalClass$InternalClass",
				c1.getQualifiedName());
		assertEquals("spoon.test.limits.utils", c1.getPackage()
				.getQualifiedName());
		assertEquals(
				spoon.test.limits.utils.ContainInternalClass.InternalClass.class,
				c1.getActualClass());

		CtClass<?> c2 = classes.get(2);
		assertEquals("InsideInternalClass", c2.getSimpleName());
		assertEquals(
				"spoon.test.limits.utils.ContainInternalClass$InternalClass$InsideInternalClass",
				c2.getQualifiedName());
		assertEquals(
				spoon.test.limits.utils.ContainInternalClass.InternalClass.InsideInternalClass.class,
				c2.getActualClass());

	}

	@Test
	public void testStaticFinalFieldInAnonymousClass() throws Exception {
		CtClass<?> type = build("spoon.test.limits.utils",
				"ContainInternalClass");
		List<CtClass<?>> classes = type.getElements(new TypeFilter<CtClass<?>>(
				CtClass.class));
		CtClass<?> c3 = classes.get(3);
		List<CtNamedElement> fields = c3
				.getElements(new NameFilter<CtNamedElement>("serialVersionUID"));
		assertEquals(1, fields.size());

	}

	@Test
	public void testInternalClassNameWithinMethod() throws Exception {
		final String nl = System.lineSeparator();

		CtClass<?> type = build("spoon.test.limits.utils",
				"ContainInternalClass");
		assertEquals("ContainInternalClass", type.getSimpleName());

		//creating and inserting a new class within the method
		CtMethod method = type.getMethod("method");
		CtClass internal = type.getFactory().Core().createClass();
		internal.setSimpleName("InternalMethodClass");
		method.getBody().insertBegin(internal);
		assertEquals("void method() {" + nl + "    class InternalMethodClass {    }" + nl + "}", method.toString());

		CtConstructorCall call = type.getFactory().Code().createConstructorCall(internal.getReference());
		internal.insertAfter(call);

		CtClass classInserted = method.getElements(new TypeFilter<CtClass>(CtClass.class)).get(0);
		assertTrue(classInserted.isLocalType());
		assertEquals("InternalMethodClass", classInserted.getSimpleName());
	}

	@Test
	public void testAnonymousClassNames() throws Exception {
		final String nl = System.lineSeparator();

		CtClass<?> type = build("spoon.test.limits.utils",
				"ContainInternalClass");
		assertEquals("ContainInternalClass", type.getSimpleName());

		List<CtNewClass> newClasses = type.getElements(new TypeFilter<CtNewClass>(CtNewClass.class));
		for (CtNewClass newClass : newClasses) {
			assertNotNull(newClass.getAnonymousClass());
			assertEquals(String.valueOf(newClasses.indexOf(newClass) + 1), newClass.getAnonymousClass().getSimpleName());
		}

		CtClass<?> anonymous = type.getFactory().Core().createClass();
		CtNewClass<?> newAnonymous = type.getFactory().Code().createNewClass(type.getReference(), anonymous);
		CtMethod<Object> method = type.getMethod("method");
		newClasses = method.getElements(new TypeFilter<CtNewClass>(CtNewClass.class));
		assertTrue(newClasses.isEmpty());
		method.getBody().insertBegin(newAnonymous);
		assertEquals("void method() {" + nl + "    new spoon.test.limits.utils.ContainInternalClass() {    };" + nl + "}", method.toString());

		newClasses = method.getElements(new TypeFilter<CtNewClass>(CtNewClass.class));
		assertFalse(newClasses.isEmpty());
		assertNotNull(newClasses.get(0).getAnonymousClass());

		Integer simpleNameOfAddedAnonymousClass = Integer.parseInt(newClasses.get(0).getAnonymousClass().getSimpleName());
		assertTrue(simpleNameOfAddedAnonymousClass > 1000);
		assertTrue(simpleNameOfAddedAnonymousClass < Integer.MAX_VALUE);
	}


}
