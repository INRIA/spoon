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
package spoon.support.visitor.java;

import com.mysema.query.support.ProjectableQuery;
import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.metamodel.MetamodelConcept;
import spoon.metamodel.Metamodel;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.path.CtElementPathBuilder;
import spoon.reflect.path.CtPathException;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Root;
import spoon.support.compiler.FileSystemFile;
import spoon.support.compiler.jdt.JDTSnippetCompiler;
import spoon.support.reflect.code.CtAssignmentImpl;
import spoon.support.reflect.code.CtConditionalImpl;
import spoon.support.reflect.declaration.CtEnumValueImpl;
import spoon.support.reflect.declaration.CtFieldImpl;
import spoon.support.visitor.equals.EqualsChecker;
import spoon.support.visitor.equals.EqualsVisitor;
import spoon.test.generics.testclasses3.ComparableComparatorBug;

import java.io.File;
import java.io.ObjectInputStream;
import java.lang.annotation.Retention;
import java.net.CookieManager;
import java.net.URLClassLoader;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.createFactory;

public class JavaReflectionTreeBuilderTest {

	@Test
	public void testScannerClass() {
		final CtClass<Class> aClass = new JavaReflectionTreeBuilder(createFactory()).scan(Class.class);
		assertNotNull(aClass);
		assertEquals("java.lang.Class", aClass.getQualifiedName());
		//The Class extends Object, but CtElementImpl (made from sources) getSuperclass() returns null. See CtTypeInformation#getSuperclass() comment.
		assertNull(aClass.getSuperclass());
		assertFalse(aClass.getSuperInterfaces().isEmpty());
		assertFalse(aClass.getFields().isEmpty());
		assertFalse(aClass.getMethods().isEmpty());
		assertFalse(aClass.getNestedTypes().isEmpty());
		assertTrue(aClass.isShadow());
	}

	@Test
	public void testScannerEnum() {
		final CtEnum<TextStyle> anEnum = new JavaReflectionTreeBuilder(createFactory()).scan(TextStyle.class);
		assertNotNull(anEnum);
		assertEquals("java.time.format.TextStyle", anEnum.getQualifiedName());
		assertNotNull(anEnum.getSuperclass());
		assertFalse(anEnum.getFields().isEmpty());
		assertFalse(anEnum.getEnumValues().isEmpty());
		assertFalse(anEnum.getMethods().isEmpty());
		assertTrue(anEnum.isShadow());
	}

	@Test
	public void testScannerInterface() {
		final CtInterface<CtLambda> anInterface = new JavaReflectionTreeBuilder(createFactory()).scan(CtLambda.class);
		assertNotNull(anInterface);
		assertEquals("spoon.reflect.code.CtLambda", anInterface.getQualifiedName());
		assertNull(anInterface.getSuperclass());
		assertFalse(anInterface.getSuperInterfaces().isEmpty());
		assertFalse(anInterface.getMethods().isEmpty());
		assertTrue(anInterface.isShadow());
	}

	@Test
	public void testScannerAnnotation() {
		final CtAnnotationType<SuppressWarnings> suppressWarning = new JavaReflectionTreeBuilder(createFactory()).scan(SuppressWarnings.class);
		assertNotNull(suppressWarning);
		assertEquals("java.lang.SuppressWarnings", suppressWarning.getQualifiedName());
		assertFalse(suppressWarning.getAnnotations().isEmpty());
		assertFalse(suppressWarning.getTypeMembers().isEmpty());
		assertTrue(suppressWarning.getTypeMembers().get(0) instanceof CtAnnotationMethod);
		assertTrue(suppressWarning.isShadow());
		assertNotNull(suppressWarning.getAnnotation(Retention.class));
		assertEquals("SOURCE", suppressWarning.getAnnotation(Retention.class).value().toString());
	}

	@Test
	public void testScannerGenericsInClass() {
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
	public void testScannerArrayReference() {
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
	public void testDeclaredMethods() {
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
	public void testDeclaredField() {
		final CtType<CookieManager> aType = new JavaReflectionTreeBuilder(createFactory()).scan(CookieManager.class);
		assertNotNull(aType);
		// CookieManager has only 2 fields. Java reflection doesn't give us field of its superclass.
		assertEquals(2, aType.getFields().size());
	}

	@Test
	public void testDeclaredConstructor() {
		final CtType<JDTSnippetCompiler> aType = new JavaReflectionTreeBuilder(createFactory()).scan(JDTSnippetCompiler.class);
		assertNotNull(aType);
		// JDTSnippetCompiler has only 1 constructor with 2 arguments but its super class has 1 constructor with 1 argument.
		assertEquals(1, ((CtClass<JDTSnippetCompiler>) aType).getConstructors().size());
	}

	@Test
	public void testShadowModelEqualsNormalModel() {
		//contract: CtType made from sources is equal to CtType made by reflection
		//with exception of CtExecutable#body, CtParameter#simpleName
		//with exception of Annotations with retention policy SOURCE
		Metamodel metaModel = new Metamodel(new File("src/main/java"));
		List<String> allProblems = new ArrayList<>();
		for (MetamodelConcept concept : metaModel.getConcepts()) {
			allProblems.addAll(checkShadowTypeIsEqual(concept.getImplementationClass()));
			allProblems.addAll(checkShadowTypeIsEqual(concept.getMetamodelInterface()));
		}
		assertTrue("Found " + allProblems.size() + " problems:\n" + String.join("\n", allProblems), allProblems.isEmpty());
	}

	private List<String> checkShadowTypeIsEqual(CtType<?> type) {
		if (type == null) {
			return Collections.emptyList();
		}
		Factory shadowFactory = createFactory();
		CtTypeReference<?> shadowTypeRef = shadowFactory.Type().createReference(type.getActualClass());
		CtType<?> shadowType = shadowTypeRef.getTypeDeclaration();

		assertFalse(type.isShadow());
		assertTrue(shadowType.isShadow());

		// Some elements, such as superinterfaces and thrown types, are ordered by their source position if they have
		// one. As a shadow model has no source positions, but a model built from source does, we must unset the source
		// positions of the normal model's elements to ensure that there are no ordering discrepancies.
		type.descendantIterator().forEachRemaining(e -> e.setPosition(SourcePosition.NOPOSITION));

		ShadowEqualsVisitor sev = new ShadowEqualsVisitor(new HashSet<>(Arrays.asList(
				//shadow classes has no body
				CtRole.STATEMENT,

				// shadow classes have no default expression
				CtRole.DEFAULT_EXPRESSION,

				// shadow classes have no comments
				CtRole.COMMENT)));

		return sev.checkDiffs(type, shadowType);
	}

	private static class Diff {
		CtElement element;
		CtElement other;
		Set<CtRole> roles = new HashSet<>();
		Diff(CtElement element, CtElement other) {
			this.element = element;
			this.other = other;
		}
	}

	private static class ShadowEqualsChecker extends EqualsChecker {
		Diff currentDiff;
		List<Diff> differences = new ArrayList<>();

		@Override
		protected void setNotEqual(CtRole role) {
			if (role == CtRole.MODIFIER) {
				if (currentDiff.element instanceof CtTypeMember) {
					CtTypeMember tm = (CtTypeMember) currentDiff.element;
					CtType<?> type = tm.getDeclaringType();
					if (type != null) {
						Set<ModifierKind> elementModifiers = ((CtModifiable) currentDiff.element).getModifiers();
						Set<ModifierKind> otherModifiers = ((CtModifiable) currentDiff.other).getModifiers();
						if (type.isInterface()) {
							if (removeModifiers(elementModifiers, ModifierKind.PUBLIC, ModifierKind.ABSTRACT, ModifierKind.FINAL)
									.equals(removeModifiers(otherModifiers, ModifierKind.PUBLIC, ModifierKind.ABSTRACT, ModifierKind.FINAL))) {
								//it is OK, that type memebers of interface differs in public abstract modifiers
								return;
							}
						} else if (type.isEnum()) {
							CtType<?> type2 = type.getDeclaringType();
							if (type2 != null) {
								if (type2.isInterface()) {
									if (removeModifiers(elementModifiers, ModifierKind.PUBLIC, ModifierKind.FINAL/*, ModifierKind.STATIC*/)
											.equals(removeModifiers(otherModifiers, ModifierKind.PUBLIC, ModifierKind.FINAL/*, ModifierKind.STATIC*/))) {
										//it is OK, that type memebers of interface differs in public abstract modifiers
										return;
									}
								}
							}
						}
					}
				}
			}
			currentDiff.roles.add(role);
		}

		private Set<ModifierKind> removeModifiers(Set<ModifierKind> elementModifiers, ModifierKind... modifiers) {
			Set<ModifierKind> copy = new HashSet<>(elementModifiers);
			for (ModifierKind modifierKind : modifiers) {
				copy.remove(modifierKind);
			}
			return copy;
		}

		@Override
		public void scan(CtElement element) {
			currentDiff = new Diff(element, other);
			super.scan(element);
			if (!currentDiff.roles.isEmpty()) {
				differences.add(currentDiff);
			}
		}
	}

	private static class ShadowEqualsVisitor extends EqualsVisitor {
		CtElement rootOfOther;
		CtElementPathBuilder pathBuilder = new CtElementPathBuilder();
		List<String> differences;
		Set<CtRole> ignoredRoles;

		ShadowEqualsVisitor(Set<CtRole> ignoredRoles) {
			super(new ShadowEqualsChecker());
			this.ignoredRoles = ignoredRoles;
		}
		List<Diff> getDiffs() {
			return ((ShadowEqualsChecker) checker).differences;
		}
		@Override
		protected boolean fail(CtRole role, Object element, Object other) {
			if (role == null) {
				this.isNotEqual = false;
				return false;
			}
			if (ignoredRoles.contains(role)) {
				this.isNotEqual = false;
				return false;
			}
			if (element instanceof CtEnumValue && role == CtRole.VALUE) {
				//CtStatementImpl.InsertType.BEFORE contains a value with nested type. Java reflection doesn't support that
				this.isNotEqual = false;
				return false;
			}

			CtElement parentOfOther = stack.peek();
			try {
				differences.add("Difference on path: " + pathBuilder.fromElement(parentOfOther, rootOfOther).toString() + "#" + role.getCamelCaseName()
				+ "\nShadow: " + String.valueOf(other)
				+ "\nNormal: " + String.valueOf(element) + "\n");
			} catch (CtPathException e) {
				throw new SpoonException(e);
			}
			return false;
		}
		@Override
		public void biScan(CtRole role, CtElement element, CtElement other) {
			if (element instanceof CtParameter) {
				CtParameter param = (CtParameter) element;
				CtParameter otherParam = (CtParameter) other;
				if (otherParam.getSimpleName().startsWith("arg")) {
					otherParam.setSimpleName(param.getSimpleName());
				}
				if (param.isFinal()) {
					//modifier final of parameters isn't accessible in runtime
					otherParam.addModifier(ModifierKind.FINAL);
				}
			}
			if (element instanceof CtAnnotation) {
				CtAnnotation myAnnotation = (CtAnnotation) element;
				if (myAnnotation.getAnnotationType().getQualifiedName().equals(Override.class.getName())) {
					return;
				}
				if (myAnnotation.getAnnotationType().getQualifiedName().equals(Root.class.getName())) {
					return;
				}
			}
			if (role == CtRole.SUPER_TYPE && other == null && element != null && ((CtTypeReference<?>) element).getQualifiedName().equals(Object.class.getName())) {
				//class X<T extends Object> cannot be distinguished in runtime from X<T>
				return;
			}
			super.biScan(role, element, other);
		}
		@Override
		protected void biScan(CtRole role, Collection<? extends CtElement> elements, Collection<? extends CtElement> others) {
			if (role == CtRole.TYPE_MEMBER) {
				//sort type members so they match together
				Map<String, CtTypeMember> elementsByName = groupTypeMembersBySignature((Collection) elements);
				Map<String, CtTypeMember> othersByName = groupTypeMembersBySignature((Collection) others);
				for (Map.Entry<String, CtTypeMember> e : elementsByName.entrySet()) {
					String name = e.getKey();
					CtTypeMember other = othersByName.remove(name);
					if (other == null) {
						if (e.getValue().isImplicit()) {
							//it is OK, that implicit elements are not available in runtime
							continue;
						}
						differences.add("Missing shadow typeMember: " + name);
					}
					biScan(role, e.getValue(), other);
				}
				for (Map.Entry<String, CtTypeMember> e : othersByName.entrySet()) {
					differences.add("Unexpected shadow typeMember: " + e.getKey());
				}
				return;
			}
			if (role == CtRole.ANNOTATION) {
				//remove all RetentionPolicy#SOURCE level annotations from elements
				List<CtAnnotation<?>> fileteredElements = ((List<CtAnnotation<?>>) elements).stream().filter(a -> {
					CtTypeReference<?> at = a.getAnnotationType();
					Class ac = at.getActualClass();
					return ac != Override.class && ac != SuppressWarnings.class && ac != Root.class;
				}).collect(Collectors.toList());
				super.biScan(role, fileteredElements, others);
				return;
			}
			super.biScan(role, elements, others);
		}
		public List<String> checkDiffs(CtType<?> type, CtType<?> shadowType) {
			differences = new ArrayList<>();
			rootOfOther = shadowType;
			biScan(null, type, shadowType);
			for (Diff diff : getDiffs()) {
				try {
					CtElement parentOf;
					CtElement rootOf;
					if (diff.other != null) {
						parentOf = diff.other.getParent();
						rootOf = rootOfOther;
					} else {
						parentOf = diff.element.getParent();
						rootOf = type;
					}
					differences.add("Diff on path: " + pathBuilder.fromElement(parentOf, rootOf).toString() + "#"
					+ diff.roles.stream().map(CtRole::getCamelCaseName).collect(Collectors.joining(", ", "[", "]"))
					+ "\nShadow: " + String.valueOf(diff.other)
					+ "\nNormal: " + String.valueOf(diff.element) + "\n");
				} catch (CtPathException e) {
					throw new SpoonException(e);
				}

			}
			return differences;
		}
	}

	private static Map<String, CtTypeMember> groupTypeMembersBySignature(Collection<CtTypeMember> typeMembers) {
		Map<String, CtTypeMember> typeMembersByName = new HashMap<>();
		for (CtTypeMember tm : typeMembers) {
			String name;
			if (tm instanceof CtExecutable) {
				CtExecutable<?> exec = ((CtExecutable) tm);
				name = exec.getSignature();
			} else {
				name = tm.getSimpleName();
			}
			CtTypeMember conflictTM = typeMembersByName.put(name, tm);
			if (conflictTM != null) {
				throw new SpoonException("There are two type members with name: " + name + " in " + tm.getParent(CtType.class).getQualifiedName());
			}
		}
		return typeMembersByName;
	}

	@Test
	public void testSuperInterfaceActualTypeArgumentsByJavaReflectionTreeBuilder() {
		final CtType<CtConditionalImpl> aType = new JavaReflectionTreeBuilder(createFactory()).scan(CtConditionalImpl.class);
		CtTypeReference<?> ifaceRef = aType.getSuperInterfaces().iterator().next();
		assertEquals(CtConditional.class.getName(), ifaceRef.getQualifiedName());
		assertEquals(1, ifaceRef.getActualTypeArguments().size());
		CtTypeReference<?> typeArg = ifaceRef.getActualTypeArguments().get(0);
		assertEquals("T", typeArg.getSimpleName());
		assertTrue(typeArg instanceof CtTypeParameterReference);
	}

	@Test
	public void testSuperInterfaceActualTypeArgumentsByCtTypeReferenceImpl() {
		TypeFactory typeFactory = createFactory().Type();
		CtTypeReference<?> aTypeRef = typeFactory.createReference(CtConditionalImpl.class);
		CtTypeReference<?> ifaceRef = aTypeRef.getSuperInterfaces().iterator().next();
		assertEquals(CtConditional.class.getName(), ifaceRef.getQualifiedName());
		assertEquals(1, ifaceRef.getActualTypeArguments().size());
		CtTypeReference<?> typeArg = ifaceRef.getActualTypeArguments().get(0);
		assertEquals("T", typeArg.getSimpleName());
		assertTrue(typeArg instanceof CtTypeParameterReference);
	}

	@Test
	public void testSuperInterfaceCorrectActualTypeArgumentsByCtTypeReferenceImpl() {
		TypeFactory typeFactory = createFactory().Type();
		CtTypeReference<?> aTypeRef = typeFactory.createReference(CtField.class);
		CtType aType = aTypeRef.getTypeDeclaration();
		for (CtTypeReference<?> ifaceRef : aType.getSuperInterfaces()) {
			for (CtTypeReference<?> actTypeRef : ifaceRef.getActualTypeArguments()) {
				if (actTypeRef instanceof CtTypeParameterReference) {
					//contract: the type parameters of super interfaces are using correct parameters from owner type
					CtTypeParameterReference actTypeParamRef = (CtTypeParameterReference) actTypeRef;
					CtTypeParameter typeParam = actTypeParamRef.getDeclaration();
					assertNotNull(typeParam);
					assertSame(aType, typeParam.getTypeParameterDeclarer());
				}
			}
		}
	}

	@Test
	public void testSuperInterfaceQName() {
		//contract: the qualified names of super interfaces are correct
		TypeFactory typeFactory = createFactory().Type();
		CtTypeReference<?> aTypeRef = typeFactory.createReference(CtExpression.class);
		CtType aType = aTypeRef.getTypeDeclaration();
		for (CtTypeReference<?> ifaceRef : aType.getSuperInterfaces()) {
			assertNotNull(ifaceRef.getQualifiedName() + " doesn't exist?", ifaceRef.getActualClass());
			assertSame(aType, ifaceRef.getParent());
		}
		for (CtTypeReference<?> ifaceRef : aTypeRef.getSuperInterfaces()) {
			assertNotNull(ifaceRef.getQualifiedName() + " doesn't exist?", ifaceRef.getActualClass());
			assertSame(aType, ifaceRef.getParent());
		}
	}

	@Test
	public void testSuperClass() {
		//contract: the super class has actual type arguments
		TypeFactory typeFactory = createFactory().Type();
		CtTypeReference<?> aTypeRef = typeFactory.createReference(CtEnumValueImpl.class);
		CtType aType = aTypeRef.getTypeDeclaration();
		CtTypeReference<?> superClass = aType.getSuperclass();
		assertEquals(CtFieldImpl.class.getName(), superClass.getQualifiedName());
		assertSame(aType, superClass.getParent());
		assertEquals(1, superClass.getActualTypeArguments().size());
		CtTypeParameterReference paramRef = (CtTypeParameterReference) superClass.getActualTypeArguments().get(0);
		assertSame(aType.getFormalCtTypeParameters().get(0), paramRef.getDeclaration());
	}

	@Test
	public void testSuperOfActualTypeArgumentsOfReturnTypeOfMethod() {

		Consumer<CtType<?>> checker = type -> {
			{
				CtMethod method = type.getMethodsByName("setAssignment").get(0);
				CtTypeReference<?> paramType = ((CtParameter<?>) method.getParameters().get(0)).getType();
				assertEquals(CtExpression.class.getName(), paramType.getQualifiedName());
				assertEquals(1, paramType.getActualTypeArguments().size());
				CtTypeParameterReference actTypeArgOfReturnType = (CtTypeParameterReference) paramType.getActualTypeArguments().get(0);
				assertEquals("A", actTypeArgOfReturnType.getSimpleName());
				CtTypeReference<?> boundType = actTypeArgOfReturnType.getBoundingType();
				//is it really correct to have bounding type T?
				//There should be NO bounding type - may be a special AST node?
				//Even the Object as bounding type here is probably not correct.
				assertEquals("T", boundType.getSimpleName());
				assertTrue(boundType instanceof CtTypeParameterReference);
			}
			{
				CtMethod method = type.getMethodsByName("getAssignment").get(0);
				CtTypeReference<?> returnType = method.getType();
				assertEquals(CtExpression.class.getName(), returnType.getQualifiedName());
				assertEquals(1, returnType.getActualTypeArguments().size());
				CtTypeParameterReference actTypeArgOfReturnType = (CtTypeParameterReference) returnType.getActualTypeArguments().get(0);
				assertEquals("A", actTypeArgOfReturnType.getSimpleName());
				CtTypeReference<?> boundType = actTypeArgOfReturnType.getBoundingType();
				//is it really correct to have bounding type T?
				//There should be NO bounding type - may be a special AST node?
				//Even the Object as bounding type here is probably not correct.
				assertEquals("T", boundType.getSimpleName());
				assertTrue(boundType instanceof CtTypeParameterReference);
			}
		};
		//try the check using CtType build from sources
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource(new FileSystemFile(new File("./src/main/java/spoon/support/reflect/code/CtAssignmentImpl.java")));
		launcher.buildModel();
		CtClass<?> classFromSources = launcher.getFactory().Class().get(CtAssignmentImpl.class.getName());
		assertFalse(classFromSources.isShadow());
		checker.accept(classFromSources);

		//try the same check using CtType build using reflection
		CtType<?> classFromReflection = createFactory().Class().get(CtAssignmentImpl.class);
		assertTrue(classFromReflection.isShadow());
		checker.accept(classFromReflection);
	}

	@Test
	public void testTypeParameterCtConditionnal() {
		// contract: when using MyClass<T> T should not have Object as superclass in shadow class

		Factory factory = createFactory();
		CtTypeReference typeReference = factory.Type().createReference(CtConditionalImpl.class);
		CtType shadowType = typeReference.getTypeDeclaration();

		assertEquals(1, shadowType.getFormalCtTypeParameters().size());
		CtTypeParameter typeParameter = shadowType.getFormalCtTypeParameters().get(0);

		assertEquals("T", typeParameter.getSimpleName());
		assertNull(typeParameter.getSuperclass());
	}

	@Test
	public void testPartialShadow() {
		// contract: the shadow class can be partially created
		Factory factory = createFactory();
		CtType<Object> type = factory.Type().get(ProjectableQuery.class);
		assertEquals("ProjectableQuery", type.getSimpleName());
		// because one of the parameter is not in the classpath therefore the reflection did not succeed to list the methods
		assertEquals(0, type.getMethods().size());
	}

	@Test
	public void testInnerClassWithConstructorParameterAnnotated() {
		Launcher launcher = new Launcher();
		launcher.addInputResource(JavaReflectionTreeBuilderTest.class
				.getClassLoader()
				.getResource("annotated-parameter-on-nested-class-constructor/Caller.java")
				.getPath());
		launcher.getEnvironment().setSourceClasspath(
				new String[]{
						"src/test/resources"
				});
		launcher.getEnvironment().setAutoImports(true);
		//contract: No error due to runtime annotation of a parameter of a constructor of a shadow nested class
		launcher.buildModel();
		Factory factory = launcher.getFactory();
		CtType caller = factory.Type().get("Caller");
		CtParameter annotatedParameter = ((CtParameter)
				((CtConstructor)
					((CtLocalVariable)
						((CtConstructor)
								caller.getTypeMembers().get(0)
						).getBody().getStatement(2)
					).getType().getTypeDeclaration().getTypeMembers().get(0)
				).getParameters().get(0));

		//contract: the annotation is correctly read
		assertEquals("Bidon", annotatedParameter.getAnnotations().get(0).getAnnotationType().getSimpleName());
	}
}
