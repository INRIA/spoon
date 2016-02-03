package spoon.test.limits.utils;

import org.junit.Test;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

import static org.junit.Assert.assertEquals;
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

}
