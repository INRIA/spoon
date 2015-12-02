package spoon.test.annotation;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import spoon.Launcher;
import spoon.processing.AbstractAnnotationProcessor;
import spoon.processing.ProcessingManager;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnnotatedElementType;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.internal.CtImplicitTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.QueueProcessingManager;
import spoon.test.TestUtils;
import spoon.test.annotation.testclasses.AnnotArray;
import spoon.test.annotation.testclasses.AnnotParamTypeEnum;
import spoon.test.annotation.testclasses.AnnotParamTypes;
import spoon.test.annotation.testclasses.AnnotationDefaultAnnotation;
import spoon.test.annotation.testclasses.AnnotationIntrospection;
import spoon.test.annotation.testclasses.AnnotationRepeated;
import spoon.test.annotation.testclasses.AnnotationsAppliedOnAnyTypeInAClass;
import spoon.test.annotation.testclasses.AnnotationsRepeated;
import spoon.test.annotation.testclasses.Bound;
import spoon.test.annotation.testclasses.Foo;
import spoon.test.annotation.testclasses.Foo.InnerAnnotation;
import spoon.test.annotation.testclasses.Foo.MiddleAnnotation;
import spoon.test.annotation.testclasses.Foo.OuterAnnotation;
import spoon.test.annotation.testclasses.GlobalAnnotation;
import spoon.test.annotation.testclasses.InnerAnnot;
import spoon.test.annotation.testclasses.Main;
import spoon.test.annotation.testclasses.TestInterface;
import spoon.test.annotation.testclasses.TypeAnnotation;

public class AnnotationTest {
	private Factory factory;

	@Before
	public void setUp() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/annotation/testclasses/",
				"-o", "./target/spooned/"
		});
		factory = launcher.getFactory();
	}

	@Test
	public void testModelBuildingAnnotationBound() throws Exception {
		CtType<?> type = this.factory.Type().get("spoon.test.annotation.testclasses.Bound");
		assertEquals("Bound", type.getSimpleName());
		assertEquals(1, type.getAnnotations().size());
	}

	@Test
	public void testWritingAnnotParamArray() throws Exception {
		CtType<?> type = this.factory.Type().get("spoon.test.annotation.testclasses.AnnotParam");
		assertEquals("@java.lang.SuppressWarnings(value = { \"unused\" , \"rawtypes\" })"
						+ DefaultJavaPrettyPrinter.LINE_SEPARATOR,
				type.getElements(new TypeFilter<>(CtAnnotation.class)).get(0).toString());
	}

	@Test
	public void testModelBuildingAnnotationBoundUsage() throws Exception {
		CtType<?> type = this.factory.Type().get("spoon.test.annotation.testclasses.Main");
		assertEquals("Main", type.getSimpleName());

		CtParameter<?> param = type.getElements(new TypeFilter<CtParameter<?>>(CtParameter.class)).get(0);
		assertEquals("a", param.getSimpleName());

		List<CtAnnotation<? extends Annotation>> annotations = param.getAnnotations();
		assertEquals(1, annotations.size());

		CtAnnotation<?> a = annotations.get(0);
		Bound actualAnnotation = (Bound) a.getActualAnnotation();
		assertEquals(8, actualAnnotation.max());
	}

	@Test
	public void testPersistenceProperty() throws Exception {
		CtType<?> type = this.factory.Type().get("spoon.test.annotation.testclasses.PersistenceProperty");
		assertEquals("PersistenceProperty", type.getSimpleName());
		assertEquals(2, type.getAnnotations().size());

		CtAnnotation<Target> a1 = type.getAnnotation(type.getFactory().Type().createReference(Target.class));
		assertNotNull(a1);

		CtAnnotation<Retention> a2 = type.getAnnotation(type.getFactory().Type().createReference(Retention.class));
		assertNotNull(a2);

		assertTrue(a1.getElementValues().containsKey("value"));
		assertTrue(a2.getElementValues().containsKey("value"));
	}

	@Test
	public void testAnnotationParameterTypes() throws Exception {
		CtType<?> type = this.factory.Type().get("spoon.test.annotation.testclasses.Main");

		CtMethod<?> m1 = type.getElements(new NameFilter<CtMethod<?>>("m1")).get(0);

		List<CtAnnotation<? extends Annotation>> annotations = m1.getAnnotations();
		assertEquals(1, annotations.size());

		CtAnnotation<?> a = annotations.get(0);
		AnnotParamTypes annot = (AnnotParamTypes) a.getActualAnnotation();
		assertEquals(42, annot.integer());
		assertEquals(1, annot.integers().length);
		assertEquals(42, annot.integers()[0]);
		assertEquals("Hello World!", annot.string());
		assertEquals(2, annot.strings().length);
		assertEquals("Hello", annot.strings()[0]);
		assertEquals("World", annot.strings()[1]);
		assertEquals(Integer.class, annot.clazz());
		assertEquals(2, annot.classes().length);
		assertEquals(Integer.class, annot.classes()[0]);
		assertEquals(String.class, annot.classes()[1]);
		assertEquals(true, annot.b());
		assertEquals('c', annot.c());
		assertEquals(42, annot.byt());
		assertEquals((short) 42, annot.s());
		assertEquals(42, annot.l());
		assertEquals(3.14f, annot.f(), 0f);
		assertEquals(3.14159, annot.d(), 0);
		assertEquals(AnnotParamTypeEnum.G, annot.e());
		assertEquals("dd", annot.ia().value());

		CtMethod<?> m2 = type.getElements(new NameFilter<CtMethod<?>>("m2")).get(0);

		annotations = m2.getAnnotations();
		assertEquals(1, annotations.size());

		a = annotations.get(0);
		annot = (AnnotParamTypes) a.getActualAnnotation();
		assertEquals(42, annot.integer());
		assertEquals(1, annot.integers().length);
		assertEquals(42, annot.integers()[0]);
		assertEquals("Hello World!", annot.string());
		assertEquals(2, annot.strings().length);
		assertEquals("Hello", annot.strings()[0]);
		assertEquals("world", annot.strings()[1]);
		assertEquals(false, annot.b());
		assertEquals(42, annot.byt());
		assertEquals((short) 42, annot.s());
		assertEquals(42, annot.l());
		assertEquals(3.14f, annot.f(), 0f);
		assertEquals(3.14159, annot.d(), 0);
		assertEquals(AnnotParamTypeEnum.G, annot.e());
		assertEquals("dd", annot.ia().value());

		// tests binary expressions
		CtMethod<?> m3 = type.getElements(new NameFilter<CtMethod<?>>("m3")).get(0);

		annotations = m3.getAnnotations();
		assertEquals(1, annotations.size());

		a = annotations.get(0);
		annot = (AnnotParamTypes) a.getActualAnnotation();
		assertEquals(45, annot.integer());
		assertEquals(2, annot.integers().length);
		assertEquals(40, annot.integers()[0]);
		assertEquals(42 * 3, annot.integers()[1]);
		assertEquals("Hello World!concatenated", annot.string());
		assertEquals(2, annot.strings().length);
		assertEquals("Helloconcatenated", annot.strings()[0]);
		assertEquals("worldconcatenated", annot.strings()[1]);
		assertEquals(true, annot.b());
		assertEquals(42 ^ 1, annot.byt());
		assertEquals((short) 42 / 2, annot.s());
		assertEquals(43, annot.l());
		assertEquals(3.14f * 2f, annot.f(), 0f);
		assertEquals(3.14159d / 3d, annot.d(), 0);
		assertEquals(AnnotParamTypeEnum.G, annot.e());
		assertEquals("dddd", annot.ia().value());
	}

	@Test
	public void testAnnotatedElementTypes() throws Exception {
		// load package of the test classes
		CtPackage pkg = this.factory.Package().get("spoon.test.annotation.testclasses");

		// check annotated element type of the package annotation
		List<CtAnnotation<?>> annotations = pkg.getAnnotations();
		assertEquals(2, annotations.size());
		assertTrue(annotations.get(0).getAnnotatedElement().equals(pkg));
		assertEquals(CtAnnotatedElementType.PACKAGE, annotations.get(0).getAnnotatedElementType());

		// load class Main from package and check annotated element type of the class annotation
		CtClass<?> clazz = pkg.getType("Main");
		assertEquals(Main.class, clazz.getActualClass());

		annotations = clazz.getAnnotations();
		assertEquals(1, annotations.size());
		assertTrue(annotations.get(0).getAnnotatedElement().equals(clazz));
		assertEquals(CtAnnotatedElementType.TYPE, clazz.getAnnotations().get(0).getAnnotatedElementType());

		// load method toString() from class and check annotated element type of the annotation
		List<CtMethod<?>> methods = clazz.getMethodsByName("toString");
		assertEquals(1, methods.size());

		CtMethod<?> method = methods.get(0);
		assertEquals("toString", method.getSimpleName());

		annotations = method.getAnnotations();
		assertEquals(1, annotations.size());
		assertTrue(annotations.get(0).getAnnotatedElement().equals(method));
		assertEquals(CtAnnotatedElementType.METHOD, annotations.get(0).getAnnotatedElementType());

		// load parameter of method m(int) and check annotated element type of the parameter annotation
		methods = clazz.getMethodsByName("m");
		assertEquals(1, methods.size());

		method = methods.get(0);
		assertEquals("m", method.getSimpleName());

		List<CtParameter<?>> parameters = method.getParameters();
		assertEquals(1, parameters.size());

		CtParameter<?> parameter = parameters.get(0);
		annotations = parameter.getAnnotations();
		assertEquals(1, annotations.size());
		assertTrue(annotations.get(0).getAnnotatedElement().equals(parameter));
		assertEquals(CtAnnotatedElementType.PARAMETER, annotations.get(0).getAnnotatedElementType());

		// load constructor of the clazz and check annotated element type of the constructor annotation
		Set<? extends CtConstructor<?>> constructors = clazz.getConstructors();
		assertEquals(1, constructors.size());

		CtConstructor<?> constructor = constructors.iterator().next();
		annotations = constructor.getAnnotations();
		assertEquals(1, annotations.size());
		assertTrue(annotations.get(0).getAnnotatedElement().equals(constructor));
		assertEquals(CtAnnotatedElementType.CONSTRUCTOR, annotations.get(0).getAnnotatedElementType());

		// load value ia of the m1() method annotation, which is also an annotation
		// and check the annotated element type of the inner annotation.
		methods = clazz.getMethodsByName("m1");
		assertEquals(1, methods.size());

		method = methods.get(0);
		annotations = method.getAnnotations();
		assertEquals(1, annotations.size());

		CtAnnotation<?> annotation = annotations.get(0);
		assertTrue(annotations.get(0).getAnnotatedElement().equals(method));
		assertEquals(CtAnnotatedElementType.METHOD, annotations.get(0).getAnnotatedElementType());

		Object element = annotation.getElementValues().get("ia");
		assertNotNull(element);
		assertTrue(element instanceof CtAnnotation);
		assertTrue(((CtAnnotation<?>) element).getAnnotatedElement().equals(annotation));
		assertEquals(CtAnnotatedElementType.ANNOTATION_TYPE, ((CtAnnotation<?>) element).getAnnotatedElementType());

		// load enum AnnotParamTypeEnum and check the annotated element type of the annotation of the enum and of the fields
		CtEnum<?> enumeration = pkg.getType("AnnotParamTypeEnum");
		assertEquals(AnnotParamTypeEnum.class, enumeration.getActualClass());

		annotations = enumeration.getAnnotations();
		assertEquals(1, annotations.size());
		assertTrue(annotations.get(0).getAnnotatedElement().equals(enumeration));
		assertEquals(CtAnnotatedElementType.TYPE, annotations.get(0).getAnnotatedElementType());

		List<CtField<?>> fields = enumeration.getValues();
		assertEquals(3, fields.size());

		annotations = fields.get(0).getAnnotations();
		assertEquals(1, annotations.size());
		assertTrue(annotations.get(0).getAnnotatedElement().equals(fields.get(0)));
		assertEquals(CtAnnotatedElementType.FIELD, annotations.get(0).getAnnotatedElementType());

		// load interface type TestInterface and check the annotated element type of the annotation
		CtInterface<?> ctInterface = pkg.getType("TestInterface");
		assertEquals(TestInterface.class, ctInterface.getActualClass());

		annotations = ctInterface.getAnnotations();
		assertEquals(1, annotations.size());
		assertTrue(annotations.get(0).getAnnotatedElement().equals(ctInterface));
		assertEquals(CtAnnotatedElementType.TYPE, annotations.get(0).getAnnotatedElementType());

		// load annotation type Bound and check the annotated element type of the annotations
		CtAnnotationType<?> annotationType = pkg.getType("Bound");
		assertEquals(Bound.class, annotationType.getActualClass());
		assertNull(annotationType.getSuperclass());
		assertEquals(0,annotationType.getAllMethods().size());
		assertEquals(0,annotationType.getSuperInterfaces().size());

		annotations = annotationType.getAnnotations();
		assertEquals(1, annotations.size());
		assertTrue(annotations.get(0).getAnnotatedElement().equals(annotationType));
		assertEquals(CtAnnotatedElementType.ANNOTATION_TYPE, annotations.get(0).getAnnotatedElementType());
	}

	@Test
	public void testAnnotationWithDefaultArrayValue() throws Throwable {
		final String res = "java.lang.Class<?>[] value() default {  };";

		CtType<?> type = this.factory.Type().get("spoon.test.annotation.testclasses.AnnotArrayInnerClass");
		CtType<?> annotationInnerClass = type.getNestedType("Annotation");
		assertEquals("Annotation", annotationInnerClass.getSimpleName());
		assertEquals(1, annotationInnerClass.getAnnotations().size());
		assertEquals(res, annotationInnerClass.getField("value").toString());

		CtType<?> annotation = this.factory.Type().get("spoon.test.annotation.testclasses.AnnotArray");
		assertEquals("AnnotArray", annotation.getSimpleName());
		assertEquals(1, annotation.getAnnotations().size());
		assertEquals(res, annotation.getField("value").toString());
	}

	@Test
	public void testInnerAnnotationsWithArray() throws Exception {
		final CtClass<?> ctClass = (CtClass<?>) this.factory.Type().get("spoon.test.annotation.testclasses.Foo");
		final CtMethod<?> testMethod = ctClass.getMethodsByName("test").get(0);
		final List<CtAnnotation<? extends Annotation>> testMethodAnnotations = testMethod.getAnnotations();
		assertEquals(1, testMethodAnnotations.size());

		final CtAnnotation<? extends Annotation> firstAnnotation = testMethodAnnotations.get(0);
		assertEquals(OuterAnnotation.class, getActualClassFromAnnotation(firstAnnotation));

		final CtNewArray<?> arrayAnnotations = (CtNewArray<?>) firstAnnotation.getElementValues().get("value");
		assertEquals(2, arrayAnnotations.getElements().size());

		final CtAnnotation<?> firstAnnotationInArray = getMiddleAnnotation(arrayAnnotations, 0);
		assertEquals(MiddleAnnotation.class, getActualClassFromAnnotation(firstAnnotationInArray));

		final CtAnnotation<?> secondAnnotationInArray = getMiddleAnnotation(arrayAnnotations, 1);
		assertEquals(MiddleAnnotation.class, getActualClassFromAnnotation(secondAnnotationInArray));

		final CtAnnotation<?> innerAnnotationInFirstMiddleAnnotation = getInnerAnnotation(firstAnnotationInArray);
		assertEquals(InnerAnnotation.class, getActualClassFromAnnotation(innerAnnotationInFirstMiddleAnnotation));
		assertEquals("hello", getLiteralValueInAnnotation(innerAnnotationInFirstMiddleAnnotation).getValue());

		final CtAnnotation<?> innerAnnotationInSecondMiddleAnnotation = getInnerAnnotation(secondAnnotationInArray);
		assertEquals(InnerAnnotation.class, getActualClassFromAnnotation(innerAnnotationInSecondMiddleAnnotation));
		assertEquals("hello again", getLiteralValueInAnnotation(innerAnnotationInSecondMiddleAnnotation).getValue());
	}

	@Test
	public void testAccessAnnotationValue() throws Exception {
		final CtClass<?> ctClass = (CtClass<?>) this.factory.Type().get("spoon.test.annotation.testclasses.Main");
		CtMethod<?> testMethod = ctClass.getMethodsByName("testValueWithArray").get(0);
		Class<?>[] value = testMethod.getAnnotation(AnnotArray.class).value();
		assertArrayEquals(new Class[] { RuntimeException.class }, value);

		testMethod = ctClass.getMethodsByName("testValueWithoutArray").get(0);
		value = testMethod.getAnnotation(AnnotArray.class).value();
		assertArrayEquals(new Class[] { RuntimeException.class }, value);
	}

	@Test
	public void testUsageOfTypeAnnotationInNewInstance() throws Exception {
		final CtClass<?> ctClass = (CtClass<?>) this.factory.Type().get("spoon.test.annotation.testclasses.AnnotationsAppliedOnAnyTypeInAClass");

		final CtConstructorCall<?> ctConstructorCall = ctClass.getElements(new AbstractFilter<CtConstructorCall<?>>(CtConstructorCall.class) {
			@Override
			public boolean matches(CtConstructorCall<?> element) {
				return "String".equals(element.getType().getSimpleName());
			}
		}).get(0);
		final List<CtAnnotation<? extends Annotation>> typeAnnotations = ctConstructorCall.getType().getAnnotations();

		assertEquals("Type of the new class must use an annotation", 1, typeAnnotations.size());
		assertEquals("Type of the new class is typed by TypeAnnotation", TypeAnnotation.class, typeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, typeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("New class with an type annotation must be well printed", "new java.lang.@spoon.test.annotation.testclasses.TypeAnnotation String()", ctConstructorCall.toString());
	}

	@Test
	public void testUsageOfTypeAnnotationInCast() throws Exception {
		final CtClass<?> ctClass = (CtClass<?>) this.factory.Type().get("spoon.test.annotation.testclasses.AnnotationsAppliedOnAnyTypeInAClass");

		final CtReturn<?> returns = ctClass.getElements(new AbstractFilter<CtReturn<?>>(CtReturn.class) {
			@Override
			public boolean matches(CtReturn<?> element) {
				return !element.getReturnedExpression().getTypeCasts().isEmpty();
			}
		}).get(0);
		final CtExpression<?> returnedExpression = returns.getReturnedExpression();
		final List<CtAnnotation<? extends Annotation>> typeAnnotations = returnedExpression.getTypeCasts().get(0).getAnnotations();

		assertEquals("Cast with a type annotation must have it in its model", 1, typeAnnotations.size());
		assertEquals("Type annotation in the cast must be typed by TypeAnnotation", TypeAnnotation.class, typeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, typeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("Cast with an type annotation must be well printed", "((java.lang.@spoon.test.annotation.testclasses.TypeAnnotation String)(s))", returnedExpression.toString());
	}

	@Test
	public void testUsageOfTypeAnnotationBeforeExceptionInSignatureOfMethod() throws Exception {
		final CtClass<?> ctClass = (CtClass<?>) this.factory.Type().get("spoon.test.annotation.testclasses.AnnotationsAppliedOnAnyTypeInAClass");

		final CtMethod<?> method = ctClass.getMethodsByName("m").get(0);
		final CtTypeReference<?> thrownReference = method.getThrownTypes().toArray(new CtTypeReference<?>[0])[0];
		final List<CtAnnotation<? extends Annotation>> typeAnnotations = thrownReference.getAnnotations();

		assertEquals("Thrown type with a type annotation must have it in its model", 1, typeAnnotations.size());
		assertEquals("Type annotation with the thrown type must be typed by TypeAnnotation", TypeAnnotation.class, typeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, typeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("Thrown type with an type annotation must be well printed", "public void m() throws java.lang.@spoon.test.annotation.testclasses.TypeAnnotation Exception {"
						+ System.lineSeparator() + "}", method.toString());
	}

	@Test
	public void testUsageOfTypeAnnotationInReturnTypeInMethod() throws Exception {
		final CtClass<?> ctClass = (CtClass<?>) this.factory.Type().get("spoon.test.annotation.testclasses.AnnotationsAppliedOnAnyTypeInAClass");

		final CtMethod<?> method = ctClass.getMethodsByName("m3").get(0);
		final CtTypeReference<?> returnReference = method.getType();
		final List<CtAnnotation<? extends Annotation>> typeAnnotations = returnReference.getAnnotations();

		assertEquals("Return type with a type annotation must have it in its model", 1, typeAnnotations.size());
		assertEquals("Type annotation with the return type must be typed by TypeAnnotation", TypeAnnotation.class, typeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, typeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("Return type with an type annotation must be well printed", "public java.lang.@spoon.test.annotation.testclasses.TypeAnnotation String m3() {"
						+ System.lineSeparator()
						+ "    return \"\";"
						+ System.lineSeparator() + "}", method.toString());
	}

	@Test
	public void testUsageOfTypeAnnotationOnParameterInMethod() throws Exception {
		final CtClass<?> ctClass = (CtClass<?>) this.factory.Type().get(AnnotationsAppliedOnAnyTypeInAClass.class);

		final CtMethod<?> method = ctClass.getMethodsByName("m6").get(0);
		final CtParameter<?> ctParameter = method.getParameters().get(0);
		final CtTypeReference<?> parameterType = ctParameter.getType();
		final List<CtAnnotation<? extends Annotation>> typeAnnotations = parameterType.getTypeAnnotations();

		assertEquals("Parameter type with a type annotation must have it in its model", 1, typeAnnotations.size());
		assertEquals("Type annotation with the parameter type must be typed by TypeAnnotation", TypeAnnotation.class, typeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, typeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("Parameter type with an type annotation must be well printed", "java.lang.@spoon.test.annotation.testclasses.TypeAnnotation String param", ctParameter.toString());
	}

	@Test
	public void testUsageOfTypeAnnotationOnLocalVariableInMethod() throws Exception {
		final CtClass<?> ctClass = (CtClass<?>) this.factory.Type().get(AnnotationsAppliedOnAnyTypeInAClass.class);

		final CtMethod<?> method = ctClass.getMethodsByName("m6").get(0);
		final CtLocalVariable<?> ctLocalVariable = method.getBody().getElements(new AbstractFilter<CtLocalVariable<?>>(CtLocalVariable.class) {
			@Override
			public boolean matches(CtLocalVariable<?> element) {
				return true;
			}
		}).get(0);
		final CtTypeReference<?> localVariableType = ctLocalVariable.getType();
		final List<CtAnnotation<? extends Annotation>> typeAnnotations = localVariableType.getTypeAnnotations();

		assertEquals("Local variable type with a type annotation must have it in its model", 1, typeAnnotations.size());
		assertEquals("Type annotation with the local variable type must be typed by TypeAnnotation", TypeAnnotation.class, typeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, typeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("Local variable type with an type annotation must be well printed", "java.lang.@spoon.test.annotation.testclasses.TypeAnnotation String s = \"\"", ctLocalVariable.toString());
	}

	@Test
	public void testUsageOfTypeAnnotationInExtendsImplementsOfAClass() throws Exception {
		final CtClass<?> ctClass = (CtClass<?>) this.factory.Type().get("spoon.test.annotation.testclasses.AnnotationsAppliedOnAnyTypeInAClass");

		final CtClass<?> innerClass = ctClass.getElements(new NameFilter<CtClass<?>>("DummyClass")).get(0);
		final CtTypeReference<?> extendsActual = innerClass.getSuperclass();
		final List<CtAnnotation<? extends Annotation>> extendsTypeAnnotations = extendsActual.getTypeAnnotations();
		final String superClassExpected = "spoon.test.annotation.testclasses.@spoon.test.annotation.testclasses.TypeAnnotation AnnotArrayInnerClass";
		assertEquals("Extends with a type annotation must have it in its model", 1, extendsTypeAnnotations.size());
		assertEquals("Type annotation on a extends must be typed by TypeAnnotation", TypeAnnotation.class, extendsTypeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, extendsTypeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("Extends with an type annotation must be well printed", superClassExpected, extendsActual.toString());

		final Set<CtTypeReference<?>> superInterfaces = innerClass.getSuperInterfaces();
		final CtTypeReference<?> firstSuperInterface = superInterfaces.toArray(new CtTypeReference<?>[0])[0];
		final List<CtAnnotation<? extends Annotation>> implementsTypeAnnotations = firstSuperInterface.getTypeAnnotations();
		final String superInterfaceExpected = "spoon.test.annotation.testclasses.@spoon.test.annotation.testclasses.TypeAnnotation BasicAnnotation";
		assertEquals("Implements with a type annotation must have it in its model", 1, implementsTypeAnnotations.size());
		assertEquals("Type annotation on a extends must be typed by TypeAnnotation", TypeAnnotation.class, implementsTypeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, implementsTypeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("Extends with an type annotation must be well printed", superInterfaceExpected, firstSuperInterface.toString());

		final CtEnum<?> enumActual = ctClass.getElements(new NameFilter<CtEnum<?>>("DummyEnum")).get(0);
		final Set<CtTypeReference<?>> superInterfacesOfEnum = enumActual.getSuperInterfaces();
		final CtTypeReference<?> firstSuperInterfaceOfEnum = superInterfacesOfEnum.toArray(new CtTypeReference<?>[0])[0];
		final List<CtAnnotation<? extends Annotation>> enumTypeAnnotations = firstSuperInterfaceOfEnum.getTypeAnnotations();
		final String enumExpected = "public enum DummyEnum implements spoon.test.annotation.testclasses.@spoon.test.annotation.testclasses.TypeAnnotation BasicAnnotation {" + System.lineSeparator() + "    ;" + System.lineSeparator() + "}";
		assertEquals("Implements in a enum with a type annotation must have it in its model", 1, enumTypeAnnotations.size());
		assertEquals("Type annotation on a implements in a enum must be typed by TypeAnnotation", TypeAnnotation.class, enumTypeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, enumTypeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("Implements in a enum with an type annotation must be well printed", enumExpected, enumActual.toString());

		final CtInterface<?> interfaceActual = ctClass.getElements(new NameFilter<CtInterface<?>>("DummyInterface")).get(0);
		final Set<CtTypeReference<?>> superInterfacesOfInterface = interfaceActual.getSuperInterfaces();
		final CtTypeReference<?> firstSuperInterfaceOfInterface = superInterfacesOfInterface.toArray(new CtTypeReference<?>[0])[0];
		final List<CtAnnotation<? extends Annotation>> interfaceTypeAnnotations = firstSuperInterfaceOfInterface.getTypeAnnotations();
		final String interfaceExpected = "public interface DummyInterface extends spoon.test.annotation.testclasses.@spoon.test.annotation.testclasses.TypeAnnotation BasicAnnotation {}";
		assertEquals("Implements in a interface with a type annotation must have it in its model", 1, interfaceTypeAnnotations.size());
		assertEquals("Type annotation on a implements in a enum must be typed by TypeAnnotation", TypeAnnotation.class, interfaceTypeAnnotations.get(0).getAnnotationType().getActualClass());
		assertEquals(CtAnnotatedElementType.TYPE_USE, interfaceTypeAnnotations.get(0).getAnnotatedElementType());
		assertEquals("Implements in a interface with an type annotation must be well printed", interfaceExpected, interfaceActual.toString());
	}

	@Test
	public void testUsageOfTypeAnnotationWithGenericTypesInClassDeclaration() throws Exception {
		final CtClass<?> ctClass = (CtClass<?>) this.factory.Type().get("spoon.test.annotation.testclasses.AnnotationsAppliedOnAnyTypeInAClass");

		final CtClass<?> genericClass = ctClass.getElements(new NameFilter<CtClass<?>>("DummyGenericClass")).get(0);
		final List<CtTypeReference<?>> formalTypeParameters = genericClass.getFormalTypeParameters();
		assertEquals("Generic class has 2 generics parameters.", 2, formalTypeParameters.size());
		assertEquals("First generic type must have type annotation", "@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator() + "T", formalTypeParameters.get(0).toString());
		assertEquals("Second generic type must have type annotation", "@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator() + "K", formalTypeParameters.get(1).toString());

		final CtTypeReference<?> superInterface = genericClass.getSuperInterfaces().toArray(new CtTypeReference<?>[0])[0];
		final String expected = "spoon.test.annotation.testclasses.BasicAnnotation<@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator() + "T>";
		assertEquals("Super interface has a generic type with type annotation", expected, superInterface.toString());
	}

	@Test
	public void testUsageOfTypeAnnotationWithGenericTypesInStatements() throws Exception {
		final CtClass<?> ctClass = (CtClass<?>) this.factory.Type().get("spoon.test.annotation.testclasses.AnnotationsAppliedOnAnyTypeInAClass");

		final CtMethod<?> method = ctClass.getMethodsByName("m4").get(0);
		final List<CtTypeReference<?>> formalTypeParameters = method.getFormalTypeParameters();
		assertEquals("Method has 1 generic parameter", 1, formalTypeParameters.size());
		assertEquals("Method with an type annotation must be well printed",
					 "@spoon.test.annotation.testclasses.TypeAnnotation" + System.lineSeparator()
							 + "T", formalTypeParameters.get(0).toString());

		final CtBlock<?> body = method.getBody();
		final String expectedFirstStatement =
				"java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation" +
						System.lineSeparator() + "T> list = new java.util.ArrayList<>()";
		final CtStatement firstStatement = body.getStatement(0);
		assertEquals("Type annotation on generic parameter declared in the method",
					 expectedFirstStatement, firstStatement.toString());
		final CtConstructorCall firstConstructorCall =
				firstStatement.getElements(new TypeFilter<CtConstructorCall>(CtConstructorCall.class))
							  .get(0);
		final CtTypeReference<?> firstTypeReference = firstConstructorCall.getType()
																		  .getActualTypeArguments()
																		  .get(0);
		assertTrue(firstTypeReference instanceof CtImplicitTypeReference);
		assertEquals("T", firstTypeReference.getSimpleName());

		final String expectedSecondStatement =
				"java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation" +
						System.lineSeparator() + "?> list2 = new java.util.ArrayList<>()";
		final CtStatement secondStatement = body.getStatement(1);
		assertEquals("Wildcard with an type annotation must be well printed",
					 expectedSecondStatement, secondStatement.toString());
		final CtConstructorCall secondConstructorCall =
				secondStatement.getElements(new TypeFilter<CtConstructorCall>(CtConstructorCall.class))
							   .get(0);
		final CtTypeReference<?> secondTypeReference = secondConstructorCall.getType()
																			.getActualTypeArguments()
																			.get(0);
		assertTrue(secondTypeReference instanceof CtImplicitTypeReference);
		assertEquals("Object", secondTypeReference.getSimpleName());

		final String expectedThirdStatement = "java.util.List<spoon.test.annotation.testclasses.@spoon.test.annotation.testclasses.TypeAnnotation BasicAnnotation> list3 = new java.util.ArrayList<spoon.test.annotation.testclasses.@spoon.test.annotation.testclasses.TypeAnnotation BasicAnnotation>()";
		assertEquals("Type in generic parameter with an type annotation must be well printed", expectedThirdStatement, body.getStatement(2).toString());
	}

	@Test
	public void testUsageOfParametersInTypeAnnotation() throws Exception {
		final CtClass<?> ctClass = (CtClass<?>) this.factory.Type().get("spoon.test.annotation.testclasses.AnnotationsAppliedOnAnyTypeInAClass");
		final CtMethod<?> method = ctClass.getMethodsByName("m5").get(0);

		final String integerParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(integer = (int)1)" + System.lineSeparator() + "T> list";
		assertEquals("integer parameter in type annotation", integerParam, method.getBody().getStatement(0).toString());

		final String arrayIntegerParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(integers = {(int)1})" + System.lineSeparator() + "T> list2";
		assertEquals("array of integers parameter in type annotation", arrayIntegerParam, method.getBody().getStatement(1).toString());

		final String stringParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(string = (String)\"\")" + System.lineSeparator() + "T> list3";
		assertEquals("string parameter in type annotation", stringParam, method.getBody().getStatement(2).toString());

		final String arrayStringParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(strings = {(String)\"\"})" + System.lineSeparator() + "T> list4";
		assertEquals("array of strings parameter in type annotation", arrayStringParam, method.getBody().getStatement(3).toString());

		final String classParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(clazz = java.lang.String.class)" + System.lineSeparator() + "T> list5";
		assertEquals("class parameter in type annotation", classParam, method.getBody().getStatement(4).toString());

		final String arrayClassParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(classes = {java.lang.String.class})" + System.lineSeparator() + "T> list6";
		assertEquals("array of classes parameter in type annotation", arrayClassParam, method.getBody().getStatement(5).toString());

		final String primitiveParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(b = (boolean)true)" + System.lineSeparator() + "T> list7";
		assertEquals("primitive parameter in type annotation", primitiveParam, method.getBody().getStatement(6).toString());

		final String enumParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(e = spoon.test.annotation.testclasses.AnnotParamTypeEnum.R)" + System.lineSeparator() + "T> list8";
		assertEquals("enum parameter in type annotation", enumParam, method.getBody().getStatement(7).toString());

		final String annotationParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(ia = @spoon.test.annotation.testclasses.InnerAnnot(value = (String)\"\"))" + System.lineSeparator() + "T> list9";
		assertEquals("annotation parameter in type annotation", annotationParam, method.getBody().getStatement(8).toString());

		final String arrayAnnotationParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(ias = {@spoon.test.annotation.testclasses.InnerAnnot(value = (String)\"\")})" + System.lineSeparator() + "T> list10";
		assertEquals("array of annotations parameter in type annotation", arrayAnnotationParam, method.getBody().getStatement(9).toString());

		final String complexArrayParam = "java.util.List<@spoon.test.annotation.testclasses.TypeAnnotation(inceptions = {@spoon.test.annotation.testclasses.Inception(value = @spoon.test.annotation.testclasses.InnerAnnot(value = (String)\"\")" + System.lineSeparator() + ", values = {@spoon.test.annotation.testclasses.InnerAnnot(value = (String)\"\")})})" + System.lineSeparator() + "T> list11";
		assertEquals("array of complexes parameters in type annotation", complexArrayParam, method.getBody().getStatement(10).toString());
	}

	@Test
	public void testOutputGeneratedByTypeAnnotation() throws Exception {
		TestUtils.canBeBuilt(new File("./target/spooned/spoon/test/annotation/testclasses/"), 8);
	}

	@Test
	public void testRepeatSameAnnotationOnClass() throws Exception {
		final CtClass<?> ctClass = (CtClass<?>) this.factory.Type().get(AnnotationsRepeated.class);

		final List<CtAnnotation<? extends Annotation>> annotations = ctClass.getAnnotations();
		assertEquals("Class must to have multi annotation of the same type", 2, annotations.size());
		assertEquals("Type of the first annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(0).getAnnotationType().getActualClass());
		assertEquals("Type of the second annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(1).getAnnotationType().getActualClass());
		assertEquals("Argument of the first annotation is \"First\"", "First", annotations.get(0).getElementValue("value"));
		assertEquals("Argument of the second annotation is \"Second\"", "Second", annotations.get(1).getElementValue("value"));
	}

	@Test
	public void testRepeatSameAnnotationOnField() throws Exception {
		final CtClass<?> ctClass = (CtClass<?>) this.factory.Type().get(AnnotationsRepeated.class);
		final CtField<?> field = ctClass.getField("field");

		final List<CtAnnotation<? extends Annotation>> annotations = field.getAnnotations();
		assertEquals("Field must to have multi annotation of the same type", 2, annotations.size());
		assertEquals("Type of the first annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(0).getAnnotationType().getActualClass());
		assertEquals("Type of the second annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(1).getAnnotationType().getActualClass());
		assertEquals("Argument of the first annotation is \"Field 1\"", "Field 1", annotations.get(0).getElementValue("value"));
		assertEquals("Argument of the second annotation is \"Field 2\"", "Field 2", annotations.get(1).getElementValue("value"));
	}

	@Test
	public void testRepeatSameAnnotationOnMethod() throws Exception {
		final CtClass<?> ctClass = (CtClass<?>) this.factory.Type().get(AnnotationsRepeated.class);
		final CtMethod<?> method = ctClass.getMethodsByName("method").get(0);

		final List<CtAnnotation<? extends Annotation>> annotations = method.getAnnotations();
		assertEquals("Method must to have multi annotation of the same type", 2, annotations.size());
		assertEquals("Type of the first annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(0).getAnnotationType().getActualClass());
		assertEquals("Type of the second annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(1).getAnnotationType().getActualClass());
		assertEquals("Argument of the first annotation is \"Method 1\"", "Method 1", annotations.get(0).getElementValue("value"));
		assertEquals("Argument of the second annotation is \"Method 2\"", "Method 2", annotations.get(1).getElementValue("value"));
	}

	@Test
	public void testRepeatSameAnnotationOnConstructor() throws Exception {
		final CtClass<?> ctClass = (CtClass<?>) this.factory.Type().get(AnnotationsRepeated.class);
		final CtConstructor<?> ctConstructor = ctClass.getConstructors().toArray(new CtConstructor<?>[0])[0];

		final List<CtAnnotation<? extends Annotation>> annotations = ctConstructor.getAnnotations();
		assertEquals("Constructor must to have multi annotation of the same type", 2, annotations.size());
		assertEquals("Type of the first annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(0).getAnnotationType().getActualClass());
		assertEquals("Type of the second annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(1).getAnnotationType().getActualClass());
		assertEquals("Argument of the first annotation is \"Constructor 1\"", "Constructor 1", annotations.get(0).getElementValue("value"));
		assertEquals("Argument of the second annotation is \"Constructor 2\"", "Constructor 2", annotations.get(1).getElementValue("value"));
	}

	@Test
	public void testRepeatSameAnnotationOnParameter() throws Exception {
		final CtClass<?> ctClass = (CtClass<?>) this.factory.Type().get(AnnotationsRepeated.class);
		final CtMethod<?> method = ctClass.getMethodsByName("methodWithParameter").get(0);
		final CtParameter<?> ctParameter = method.getParameters().get(0);

		final List<CtAnnotation<? extends Annotation>> annotations = ctParameter.getAnnotations();
		assertEquals("Parameter must to have multi annotation of the same type", 2, annotations.size());
		assertEquals("Type of the first annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(0).getAnnotationType().getActualClass());
		assertEquals("Type of the second annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(1).getAnnotationType().getActualClass());
		assertEquals("Argument of the first annotation is \"Param 1\"", "Param 1", annotations.get(0).getElementValue("value"));
		assertEquals("Argument of the second annotation is \"Param 2\"", "Param 2", annotations.get(1).getElementValue("value"));
	}

	@Test
	public void testRepeatSameAnnotationOnLocalVariable() throws Exception {
		final CtClass<?> ctClass = (CtClass<?>) this.factory.Type().get(AnnotationsRepeated.class);
		final CtMethod<?> method = ctClass.getMethodsByName("methodWithLocalVariable").get(0);
		final CtLocalVariable<?> ctLocalVariable = method.getBody().getElements(new AbstractFilter<CtLocalVariable<?>>(CtLocalVariable.class) {
			@Override
			public boolean matches(CtLocalVariable<?> element) {
				return true;
			}
		}).get(0);

		final List<CtAnnotation<? extends Annotation>> annotations = ctLocalVariable.getAnnotations();
		assertEquals("Local variable must to have multi annotation of the same type", 2, annotations.size());
		assertEquals("Type of the first annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(0).getAnnotationType().getActualClass());
		assertEquals("Type of the second annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(1).getAnnotationType().getActualClass());
		assertEquals("Argument of the first annotation is \"Local 1\"", "Local 1", annotations.get(0).getElementValue("value"));
		assertEquals("Argument of the second annotation is \"Local 2\"", "Local 2", annotations.get(1).getElementValue("value"));
	}

	@Test
	public void testRepeatSameAnnotationOnPackage() throws Exception {
		final CtPackage pkg = this.factory.Package().get("spoon.test.annotation.testclasses");

		final List<CtAnnotation<? extends Annotation>> annotations = pkg.getAnnotations();
		assertEquals("Local variable must to have multi annotation of the same type", 2, annotations.size());
		assertEquals("Type of the first annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(0).getAnnotationType().getActualClass());
		assertEquals("Type of the second annotation is AnnotationRepeated", AnnotationRepeated.class, annotations.get(1).getAnnotationType().getActualClass());
		assertEquals("Argument of the first annotation is \"Package 1\"", "Package 1", annotations.get(0).getElementValue("value"));
		assertEquals("Argument of the second annotation is \"Package 2\"", "Package 2", annotations.get(1).getElementValue("value"));
	}

	@Test
	public void testDefaultValueInAnnotationsForAnnotationFields() throws Exception {
		final CtType<?> annotation = factory.Type().get(AnnotationDefaultAnnotation.class);

		final CtField<?> ctField = annotation.getFields().get(0);
		assertEquals("Field is typed by an annotation.", InnerAnnot.class, ctField.getType().getActualClass());
		assertEquals("Default value of a field typed by an annotation must be an annotation",
				InnerAnnot.class, ctField.getDefaultExpression().getType().getActualClass());
	}

	@Test
	public void testGetAnnotationOuter() throws Exception {
		final CtClass<?> ctClass = (CtClass<?>) this.factory.Type().get("spoon.test.annotation.testclasses.Foo");
		final CtMethod<?> testMethod = ctClass.getMethodsByName("test").get(0);
		Foo.OuterAnnotation annot = testMethod.getAnnotation(Foo.OuterAnnotation.class);
		assertNotNull(annot);
		assertEquals(2,annot.value().length);
	}

	@Test
	public void testAbstractAllAnnotationProcessor() throws Exception {
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotationsAppliedOnAnyTypeInAClass.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/BasicAnnotation.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/TypeAnnotation.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotParamTypeEnum.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/InnerAnnot.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/Inception.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/TestAnnotation.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotArrayInnerClass.java");
		factory = spoon.getFactory();
		spoon.buildModel();

		// create the processor
		final ProcessingManager p = new QueueProcessingManager(factory);
		final TypeAnnotationProcessor processor = new TypeAnnotationProcessor();
		p.addProcessor(processor);
		p.process(factory.Class().getAll());

		assertEquals(30, processor.elements.size());
	}

	@Test
	public void testAbstractAllAnnotationProcessorWithGlobalAnnotation() throws Exception {
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/ClassProcessed.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/TypeAnnotation.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/AnnotParamTypeEnum.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/InnerAnnot.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/Inception.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/GlobalAnnotation.java");
		spoon.addInputResource("./src/test/java/spoon/test/annotation/testclasses/TestAnnotation.java");
		factory = spoon.getFactory();
		spoon.buildModel();

		// create the processor
		final ProcessingManager p = new QueueProcessingManager(factory);
		final GlobalProcessor processor = new GlobalProcessor();
		p.addProcessor(processor);
		final TypeAnnotationMethodProcessor methodProcessor = new TypeAnnotationMethodProcessor();
		p.addProcessor(methodProcessor);
		p.process(factory.Class().getAll());

		assertEquals(5, processor.elements.size());
		assertEquals(2, methodProcessor.elements.size());
	}

	@Test
	public void testAnnotationIntrospection() throws Exception {
		CtClass<Object> aClass = factory.Class().get(AnnotationIntrospection.class);
		CtMethod<?> mMethod = aClass.getMethod("m");
		CtStatement statement = mMethod.getBody().getStatement(1);
		assertEquals("annotation.equals(null)", statement.toString());
	}

	abstract class AbstractElementsProcessor<A extends Annotation, E extends CtElement>
			extends AbstractAnnotationProcessor<A, E> {
		final List<CtElement> elements = new ArrayList<>();
		@Override
		public void process(A annotation, E element) {
			elements.add(element);
		}
	}

	class GlobalProcessor extends AbstractElementsProcessor<GlobalAnnotation, CtElement> {
		@Override
		public void process(GlobalAnnotation annotation, CtElement element) {
			super.process(annotation, element);
		}
	}

	class TypeAnnotationProcessor extends AbstractElementsProcessor<TypeAnnotation, CtElement> {
		@Override
		public void process(TypeAnnotation annotation, CtElement element) {
			super.process(annotation, element);
		}
	}

	class TypeAnnotationMethodProcessor extends AbstractElementsProcessor<TypeAnnotation, CtTypeReference<?>> {
		@Override
		public void process(TypeAnnotation annotation, CtTypeReference<?> element) {
			if (element.getParent() instanceof CtMethod) {
				super.process(annotation, element);
			}
		}
	}

	public static Class<? extends Annotation> getActualClassFromAnnotation(CtAnnotation<? extends Annotation> annotation) {
		return annotation.getAnnotationType().getActualClass();
	}

	private CtLiteral<?> getLiteralValueInAnnotation(CtAnnotation<?> annotation) {
		return (CtLiteral<?>) annotation.getElementValues().get("value");
	}

	private CtAnnotation<?> getInnerAnnotation(CtAnnotation<?> firstAnnotationInArray) {
		return (CtAnnotation<?>) firstAnnotationInArray.getElementValues().get("value");
	}

	private CtAnnotation<?> getMiddleAnnotation(CtNewArray<?> arrayAnnotations, int index) {
		return (CtAnnotation<?>) arrayAnnotations.getElements().get(index);
	}
}
