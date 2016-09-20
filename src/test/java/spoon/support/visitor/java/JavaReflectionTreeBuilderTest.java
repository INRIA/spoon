package spoon.support.visitor.java;

import org.junit.Test;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.support.compiler.jdt.JDTSnippetCompiler;
import spoon.test.generics.ComparableComparatorBug;

import java.io.ObjectInputStream;
import java.net.CookieManager;
import java.net.URLClassLoader;
import java.time.format.TextStyle;
import java.util.stream.Collectors;

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
		assertNotNull(anEnum.getSuperclass());
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

		// New type parameter declaration.
		assertEquals(1, aType.getFormalCtTypeParameters().size());
		CtTypeParameter ctTypeParameter = aType.getFormalCtTypeParameters().get(0);
		assertEquals("E extends java.lang.Comparable<? super E>", ctTypeParameter.toString());
		assertEquals(1, ctTypeParameter.getSuperclass().getActualTypeArguments().size());
		assertTrue(ctTypeParameter.getSuperclass().getActualTypeArguments().get(0) instanceof CtTypeParameterReference);
		assertEquals("? super E", ctTypeParameter.getSuperclass().getActualTypeArguments().get(0).toString());
	}

	@Test
	public void testScannerArrayReference() throws Exception {
		final CtType<URLClassLoader> aType = new JavaReflectionTreeBuilder(createFactory()).scan(URLClassLoader.class);
		assertNotNull(aType);
		final CtMethod<Object> aMethod = aType.getMethod("getURLs");
		assertTrue(aMethod.getType() instanceof CtArrayTypeReference);
		final CtArrayTypeReference<Object> arrayRef = (CtArrayTypeReference<Object>) aMethod.getType();
		assertNull(arrayRef.getPackage());
		assertNull(arrayRef.getDeclaringType());
		assertNotNull(arrayRef.getComponentType());
	}

	@Test
	public void testDeclaredMethods() throws Exception {
		final CtType<StringBuilder> type = new JavaReflectionTreeBuilder(createFactory()).scan(StringBuilder.class);
		assertNotNull(type);
		// All methods overridden from AbstractStringBuilder and with a type changed have been removed.
		assertEquals(0, type.getMethods().stream().filter(ctMethod -> "java.lang.AbstractStringBuilder".equals(ctMethod.getType().getQualifiedName())).collect(Collectors.toList()).size());
		// reverse is declared in AbstractStringBuilder and overridden in StringBuilder but the type is the same.
		assertNotNull(type.getMethod("reverse"));
		// readObject is declared in StringBuilder.
		assertNotNull(type.getMethod("readObject", type.getFactory().Type().createReference(ObjectInputStream.class)));
	}

	@Test
	public void testDeclaredField() throws Exception {
		final CtType<CookieManager> aType = new JavaReflectionTreeBuilder(createFactory()).scan(CookieManager.class);
		assertNotNull(aType);
		// CookieManager have only 2 fields. Java reflection doesn't give us field of its superclass.
		assertEquals(2, aType.getFields().size());
	}

	@Test
	public void testDeclaredConstructor() throws Exception {
		final CtType<JDTSnippetCompiler> aType = new JavaReflectionTreeBuilder(createFactory()).scan(JDTSnippetCompiler.class);
		assertNotNull(aType);
		// JDTSnippetCompiler have only 1 constructor with 2 arguments but its super class have 1 constructor with 1 argument.
		assertEquals(1, ((CtClass<JDTSnippetCompiler>) aType).getConstructors().size());
	}
}
