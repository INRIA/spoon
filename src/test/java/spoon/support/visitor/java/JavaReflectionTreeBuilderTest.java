package spoon.support.visitor.java;

import org.junit.Test;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.test.generics.ComparableComparatorBug;

import java.time.format.TextStyle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.createFactory;

public class JavaReflectionTreeBuilderTest {
	@Test
	public void testScannerClass() throws Exception {
		final CtClass<Class> aClass = new JavaReflectionTreeBuilder(createFactory()).scan(Class.class);
		assertNotNull(aClass);
		assertEquals("java.lang.Class", aClass.getQualifiedName());
		assertNotNull(aClass.getSuperclass());
		assertTrue(aClass.getSuperInterfaces().size() > 0);
		assertTrue(aClass.getFields().size() > 0);
		assertTrue(aClass.getMethods().size() > 0);
		assertTrue(aClass.getNestedTypes().size() > 0);
		assertTrue(aClass.isShadow());
	}

	@Test
	public void testScannerEnum() throws Exception {
		final CtEnum<TextStyle> anEnum = new JavaReflectionTreeBuilder(createFactory()).scan(TextStyle.class);
		assertNotNull(anEnum);
		assertEquals("java.time.format.TextStyle", anEnum.getQualifiedName());
		assertNull(anEnum.getSuperclass());
		assertTrue(anEnum.getFields().size() > 0);
		assertTrue(anEnum.getEnumValues().size() > 0);
		assertTrue(anEnum.getMethods().size() > 0);
		assertTrue(anEnum.isShadow());
	}

	@Test
	public void testScannerInterface() throws Exception {
		final CtInterface<CtLambda> anInterface = new JavaReflectionTreeBuilder(createFactory()).scan(CtLambda.class);
		assertNotNull(anInterface);
		assertEquals("spoon.reflect.code.CtLambda", anInterface.getQualifiedName());
		assertNull(anInterface.getSuperclass());
		assertTrue(anInterface.getSuperInterfaces().size() > 0);
		assertTrue(anInterface.getMethods().size() > 0);
		assertTrue(anInterface.isShadow());
	}

	@Test
	public void testScannerAnnotation() throws Exception {
		final CtAnnotationType<SuppressWarnings> anAnnotation = new JavaReflectionTreeBuilder(createFactory()).scan(SuppressWarnings.class);
		assertNotNull(anAnnotation);
		assertEquals("java.lang.SuppressWarnings", anAnnotation.getQualifiedName());
		assertTrue(anAnnotation.getAnnotations().size() > 0);
		assertTrue(anAnnotation.getFields().size() > 0);
		assertTrue(anAnnotation.isShadow());
	}

	@Test
	public void testScannerGenericsInClass() throws Exception {
		final CtType<ComparableComparatorBug> aType = new JavaReflectionTreeBuilder(createFactory()).scan(ComparableComparatorBug.class);
		assertNotNull(aType);
		assertEquals(1, aType.getFormalTypeParameters().size());
		assertTrue(aType.getFormalTypeParameters().get(0) instanceof CtTypeParameterReference);
		CtTypeParameterReference ctTypeParameterReference = (CtTypeParameterReference) aType.getFormalTypeParameters().get(0);
		assertEquals("E extends java.lang.Comparable<? super E>", ctTypeParameterReference.toString());
		assertEquals(1, ctTypeParameterReference.getBoundingType().getActualTypeArguments().size());
		assertTrue(ctTypeParameterReference.getBoundingType().getActualTypeArguments().get(0) instanceof CtTypeParameterReference);
	}
}
