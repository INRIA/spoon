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
package spoon.test.type;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.SpoonClassNotFoundException;
import spoon.test.type.testclasses.Mole;
import spoon.test.type.testclasses.Pozole;
import spoon.test.type.testclasses.TypeMembersOrder;
import spoon.testing.utils.ModelUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.buildClass;
import static spoon.testing.utils.ModelUtils.canBeBuilt;
import static spoon.testing.utils.ModelUtils.createFactory;

public class TypeTest {
	@Test
	public void testTypeAccessForDotClass() {
		// contract: When we use .class on a type, this must be a CtTypeAccess.
		final String target = "./target/type";
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/type/testclasses");
		launcher.setSourceOutputDirectory(target);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Pozole> aPozole = launcher.getFactory().Class().get(Pozole.class);
		final CtMethod<?> make = aPozole.getMethodsByName("make").get(0);

		final List<CtFieldRead<?>> fieldClasses = make.getElements(new TypeFilter<CtFieldRead<?>>(CtFieldRead.class) {
			@Override
			public boolean matches(CtFieldRead<?> element) {
				return "class".equals(element.getVariable().getSimpleName()) && super.matches(element);
			}
		});
		assertEquals(4, fieldClasses.size());
		for (CtFieldRead<?> fieldClass : fieldClasses) {
			assertTrue(fieldClass.getTarget() instanceof CtTypeAccess);
		}

		canBeBuilt(target, 8, true);
	}

	@Test
	public void testTypeAccessOnPrimitive() {
		Factory factory = createFactory();
		CtClass<?> clazz = factory.Code().createCodeSnippetStatement( //
				"class X {" //
						+ "public void foo() {" //
						+ " Class klass=null;" //
						+ "  boolean x= (klass == short.class);" //
						+ "}};").compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		CtBlock<?> body = foo.getBody();
		CtLocalVariable<?> ass = body.getStatement(1);
		CtBinaryOperator<?> op = (CtBinaryOperator<?>) ass.getDefaultExpression();
		assertEquals("Class", op.getLeftHandOperand().getType().getSimpleName());
		assertFalse(op.getLeftHandOperand().getType().isPrimitive());
		assertEquals("Class", op.getRightHandOperand().getType().getSimpleName());
		assertTrue(op.getRightHandOperand() instanceof CtFieldRead);
		assertFalse(op.getRightHandOperand().getType().isPrimitive());
	}

	@Test
	public void testTypeAccessForTypeAccessInInstanceOf() {
		// contract: the right hand operator must be a CtTypeAccess.
		final String target = "./target/type";
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/type/testclasses");
		launcher.setSourceOutputDirectory(target);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Pozole> aPozole = launcher.getFactory().Class().get(Pozole.class);
		final CtMethod<?> eat = aPozole.getMethodsByName("eat").get(0);

		final List<CtTypeAccess<?>> typeAccesses = eat.getElements(new TypeFilter<>(CtTypeAccess.class));
		assertEquals(2, typeAccesses.size());

		assertTrue(typeAccesses.get(0).getParent() instanceof CtBinaryOperator);
		assertEquals(BinaryOperatorKind.INSTANCEOF, ((CtBinaryOperator) typeAccesses.get(0).getParent()).getKind());
		assertEquals("a instanceof java.lang.String", typeAccesses.get(0).getParent().toString());

		assertTrue(typeAccesses.get(1).getParent() instanceof CtBinaryOperator);
		assertEquals(BinaryOperatorKind.INSTANCEOF, ((CtBinaryOperator) typeAccesses.get(1).getParent()).getKind());
		assertEquals("a instanceof java.util.Collection<?>", typeAccesses.get(1).getParent().toString());
	}

	@Test
	public void testTypeAccessOfArrayObjectInFullyQualifiedName() {
		// contract: A type access in fully qualified name must to rewrite well.
		final String target = "./target/type";
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/type/testclasses");
		launcher.setSourceOutputDirectory(target);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Pozole> aPozole = launcher.getFactory().Class().get(Pozole.class);
		final CtMethod<?> season = aPozole.getMethodsByName("season").get(0);

		final List<CtTypeAccess<?>> typeAccesses = season.getElements(new TypeFilter<>(CtTypeAccess.class));
		assertEquals(2, typeAccesses.size());

		assertTrue(typeAccesses.get(0).getParent() instanceof CtBinaryOperator);
		assertEquals(BinaryOperatorKind.INSTANCEOF, ((CtBinaryOperator) typeAccesses.get(0).getParent()).getKind());
		assertEquals("a instanceof java.lang.@spoon.test.annotation.testclasses.TypeAnnotation(integer = 1)" + System.lineSeparator() + "Object[]", typeAccesses.get(0).getParent().toString());

		assertTrue(typeAccesses.get(1).getParent() instanceof CtBinaryOperator);
		assertEquals(BinaryOperatorKind.INSTANCEOF, ((CtBinaryOperator) typeAccesses.get(1).getParent()).getKind());
		assertEquals("a instanceof java.lang.Object[]", typeAccesses.get(1).getParent().toString());

		canBeBuilt(target, 8, true);
	}

	@Test
	public void testTypeAccessImplicitIsDerived() throws Exception {
		// contract: A CtTypeAccess#implicit is derived
		CtType<?> aPozole = ModelUtils.buildClass(Pozole.class);
		final CtMethod<?> season = aPozole.getMethodsByName("season").get(0);

		CtTypeAccess<?> typeAccesses = season.getElements(new TypeFilter<>(CtTypeAccess.class)).get(0);
		assertFalse(typeAccesses.isImplicit());
		assertFalse(typeAccesses.getAccessedType().isImplicit());
		//contract: setting the value on accessed type influences value on type access too
		typeAccesses.getAccessedType().setImplicit(true);
		assertTrue(typeAccesses.isImplicit());
		assertTrue(typeAccesses.getAccessedType().isImplicit());
		//contract: setting the value on type access influences value on type too
		typeAccesses.setImplicit(false);
		assertFalse(typeAccesses.isImplicit());
		assertFalse(typeAccesses.getAccessedType().isImplicit());
	}

	@Test
	public void test() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/TorIntegration.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		CtType<?> ctType = launcher.getFactory().Class().getAll().get(0);
		List<CtNewClass> elements = ctType.getElements(new TypeFilter<>(CtNewClass.class));
		assertEquals(4, elements.size());
		for (CtNewClass ctNewClass : elements) {
			assertEquals("android.content.DialogInterface$OnClickListener", ctNewClass.getAnonymousClass().getSuperclass().getQualifiedName());
		}
	}

	@Test
	public void testIntersectionTypeReferenceInGenericsAndCasts() {
		final String target = "./target/type";
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/type/testclasses");
		launcher.setSourceOutputDirectory(target);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Pozole> aPozole = launcher.getFactory().Class().get(Pozole.class);
		final CtMethod<?> prepare = aPozole.getMethodsByName("prepare").get(0);

		// Intersection type in generic types.
		final List<CtClass> localTypes = prepare.getElements(new TypeFilter<>(CtClass.class));
		assertEquals(1, localTypes.size());

		// New type parameter declaration.
		final CtTypeParameter typeParameter = localTypes.get(0).getFormalCtTypeParameters().get(0);
		assertNotNull(typeParameter);
		assertEquals("T", typeParameter.getSimpleName());
		assertIntersectionTypeForPozolePrepareMethod(aPozole, typeParameter.getSuperclass());

		// Intersection type in casts.
		final List<CtLambda<?>> lambdas = prepare.getElements(new TypeFilter<>(CtLambda.class));
		assertEquals(1, lambdas.size());

		assertEquals(1, lambdas.get(0).getTypeCasts().size());
		assertTrue(lambdas.get(0).getTypeCasts().get(0) instanceof CtIntersectionTypeReference);
		final CtIntersectionTypeReference<?> intersectionType = lambdas.get(0).getTypeCasts().get(0).asCtIntersectionTypeReference();
		assertTrue(intersectionType.toString().contains("java.lang.Runnable")
			&& intersectionType.toString().contains("java.io.Serializable"));
		CtTypeReference refRunnable = aPozole.getFactory().Type().createReference(Runnable.class);
		CtTypeReference refSerializable = aPozole.getFactory().Type().createReference(Serializable.class);
		CtTypeReference ref0 = intersectionType.getBounds().stream().collect(Collectors.toList()).get(0);
		CtTypeReference ref1 = intersectionType.getBounds().stream().collect(Collectors.toList()).get(1);
		assertTrue((ref0.equals(refRunnable) || ref0.equals(refSerializable))
			&& (ref1.equals(refRunnable) || ref1.equals(refSerializable)));

		canBeBuilt(target, 8, true);
	}

	private void assertIntersectionTypeForPozolePrepareMethod(CtClass<Pozole> aPozole, CtTypeReference<?> boundingType) {
		assertNotNull(boundingType);
		assertTrue(boundingType instanceof CtIntersectionTypeReference);
		assertEquals("java.lang.Runnable & java.io.Serializable", boundingType.toString());
		final CtIntersectionTypeReference<?> superType = boundingType.asCtIntersectionTypeReference();
		assertEquals(aPozole.getFactory().Type().createReference(Runnable.class), superType.getBounds().stream().collect(Collectors.toList()).get(0));
		assertEquals(aPozole.getFactory().Type().createReference(Serializable.class), superType.getBounds().stream().collect(Collectors.toList()).get(1));
	}

	@Test
	public void testTypeReferenceInGenericsAndCasts() {
		final String target = "./target/type";
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/type/testclasses");
		launcher.setSourceOutputDirectory(target);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Pozole> aPozole = launcher.getFactory().Class().get(Pozole.class);
		final CtMethod<?> prepare = aPozole.getMethodsByName("finish").get(0);

		// Intersection type in generic types.
		final List<CtClass> localTypes = prepare.getElements(new TypeFilter<>(CtClass.class));
		assertEquals(1, localTypes.size());

		// New type parameter declaration.
		final CtTypeParameter typeParameter = localTypes.get(0).getFormalCtTypeParameters().get(0);
		assertNotNull(typeParameter);
		assertEquals("T", typeParameter.getSimpleName());
		assertIntersectionTypeForPozoleFinishMethod(aPozole, typeParameter.getSuperclass());

		// Intersection type in casts.
		final List<CtLambda<?>> lambdas = prepare.getElements(new TypeFilter<>(CtLambda.class));
		assertEquals(1, lambdas.size());

		assertEquals(1, lambdas.get(0).getTypeCasts().size());
		assertEquals("java.lang.Runnable", lambdas.get(0).getTypeCasts().get(0).toString());
		assertEquals(aPozole.getFactory().Type().createReference(Runnable.class), lambdas.get(0).getTypeCasts().get(0));

		canBeBuilt(target, 8, true);
	}

	private void assertIntersectionTypeForPozoleFinishMethod(CtClass<Pozole> aPozole, CtTypeReference<?> boundingType) {
		assertNotNull(boundingType);
		assertEquals("java.lang.Runnable", boundingType.toString());
		assertEquals(aPozole.getFactory().Type().createReference(Runnable.class), boundingType);
	}

	@Test
	public void testIntersectionTypeOnTopLevelType() throws Exception {
		final CtType<Mole> aMole = buildClass(Mole.class);

		assertEquals(1, aMole.getFormalCtTypeParameters().size());

		// New type parameter declaration.
		final CtTypeParameter typeParameter = aMole.getFormalCtTypeParameters().get(0);
		assertIntersectionTypeForMole(aMole, typeParameter.getSuperclass());
	}

	private void assertIntersectionTypeForMole(CtType<Mole> aMole, CtTypeReference<?> boundingType) {
		assertNotNull(boundingType);
		assertTrue(boundingType instanceof CtIntersectionTypeReference);
		assertEquals(2, boundingType.asCtIntersectionTypeReference().getBounds().size());
		assertSame(Number.class, boundingType.asCtIntersectionTypeReference().getBounds().stream().collect(Collectors.toList()).get(0).getActualClass());
		assertSame(Comparable.class, boundingType.asCtIntersectionTypeReference().getBounds().stream().collect(Collectors.toList()).get(1).getActualClass());
		assertEquals("public class Mole<NUMBER extends java.lang.Number & java.lang.Comparable<NUMBER>> {}", aMole.toString());
	}

	@Test
	public void testUnboxingTypeReference() {
		// contract: When you call CtTypeReference#unbox on a class which doesn't exist
		// in the spoon path, the method return the type reference itself.
		final Factory factory = createFactory();
		final CtTypeReference<Object> aReference = factory.Type().createReference("fr.inria.Spoon");
		try {
			final CtTypeReference<?> unbox = aReference.unbox();
			assertEquals(aReference, unbox);
		} catch (SpoonClassNotFoundException e) {
			fail("Should never throw a SpoonClassNotFoundException.");
		}
	}

	@Test
	public void testDeclarationCreatedByFactory() {
		final Factory factory = createFactory();
		assertNotNull(factory.Interface().create("fr.inria.ITest").getReference().getDeclaration());
		assertNotNull(factory.Enum().create("fr.inria.ETest").getReference().getDeclaration());
	}

	@Test
	public void testPolyTypBindingInTernaryExpression() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/ternary-bug");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		CtType<Object> aType = launcher.getFactory().Type().get("de.uni_bremen.st.quide.persistence.transformators.IssueTransformator");
		CtConstructorCall ctConstructorCall = aType.getElements(new TypeFilter<CtConstructorCall>(CtConstructorCall.class) {
			@Override
			public boolean matches(CtConstructorCall element) {
				return "TOIssue".equals(element.getExecutable().getType().getSimpleName()) && super.matches(element);
			}
		}).get(0);
		assertEquals(launcher.getFactory().Type().objectType(), ctConstructorCall.getExecutable().getParameters().get(9));
	}

	@Test
	public void testShadowType() {

		/* Objects and factory have to be the sames */

		Launcher launcher = new Launcher();
		launcher.buildModel();

		final CtClass<Object> objectCtClass = launcher.getFactory().Class().get(Object.class);
		final CtClass<Object> objectCtClass1 = launcher.getFactory().Class().get(Object.class);

		assertSame(objectCtClass, objectCtClass1);

		assertSame(launcher.getFactory().Class(), objectCtClass.getFactory().Class());
		assertSame(launcher.getFactory(), objectCtClass.getFactory());

		assertSame(launcher.getFactory().Class(), objectCtClass1.getFactory().Class());
		assertSame(launcher.getFactory(), objectCtClass1.getFactory());

		assertSame(objectCtClass.getFactory().Class().get(objectCtClass.getActualClass()), objectCtClass);
		assertSame(objectCtClass.getFactory().Class().get(Object.class), objectCtClass);

		assertSame(objectCtClass1.getFactory().Class().get(objectCtClass1.getActualClass()), objectCtClass1);
		assertSame(objectCtClass1.getFactory().Class().get(Object.class), objectCtClass1);

		assertTrue(objectCtClass.isShadow());
		assertEquals("java.lang.Object", objectCtClass.getQualifiedName());

		final CtType<Object> objectCtType = launcher.getFactory().Type().get(Object.class);
		final CtType<Object> objectCtType1 = launcher.getFactory().Type().get(Object.class);

		assertSame(objectCtType, objectCtType1);

		assertSame(launcher.getFactory().Type(), objectCtType.getFactory().Type());
		assertSame(launcher.getFactory(), objectCtType.getFactory());

		assertSame(launcher.getFactory().Type(), objectCtType1.getFactory().Type());
		assertSame(launcher.getFactory(), objectCtType1.getFactory());

		assertSame(objectCtType.getFactory().Type().get(objectCtType.getActualClass()), objectCtType);
		assertSame(objectCtType.getFactory().Type().get(Object.class), objectCtType);

		assertSame(objectCtType1.getFactory().Type().get(objectCtType1.getActualClass()), objectCtType1);
		assertSame(objectCtType1.getFactory().Type().get(Object.class), objectCtType1);

		assertTrue(objectCtClass.isShadow());
		assertEquals("java.lang.Object", objectCtClass.getQualifiedName());

		final List<String> methodNameList = Arrays.asList(Object.class.getDeclaredMethods()).stream().map(Method::getName).collect(Collectors.toList());

		for (CtMethod<?> ctMethod : objectCtClass.getMethods()) {
			assertTrue(methodNameList.contains(ctMethod.getSimpleName()));
			assertTrue(ctMethod.getBody().getStatements().isEmpty());
		}

	}

	@Test
	public void testTypeMemberOrder() {
		// contract: The TypeMembers keeps order of members same like in source file 
		final String target = "./target/type";
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/type/testclasses/TypeMembersOrder.java");
		launcher.setSourceOutputDirectory(target);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		Factory f = launcher.getFactory();
		final CtClass<?> aTypeMembersOrder = f.Class().get(TypeMembersOrder.class);
		{
			List<String> typeMemberNames = new ArrayList<>();
			for (CtTypeMember typeMember : aTypeMembersOrder.getTypeMembers()) {
				typeMemberNames.add(typeMember.getSimpleName());
			}
			assertEquals(Arrays.asList("<init>", "method1", "field2", "TypeMembersOrder", "method4", "field5", "", "nestedType6", "field7", "method8"), typeMemberNames);
		}
		{
			//contract: newly added type member is at the end
			f.createMethod(aTypeMembersOrder, Collections.singleton(ModifierKind.PUBLIC), f.Type().voidType(), "method9", Collections.emptyList(), Collections.emptySet());
			List<String> typeMemberNames = new ArrayList<>();
			for (CtTypeMember typeMember : aTypeMembersOrder.getTypeMembers()) {
				typeMemberNames.add(typeMember.getSimpleName());
			}
			assertEquals(Arrays.asList("<init>", "method1", "field2", "TypeMembersOrder", "method4", "field5", "", "nestedType6", "field7", "method8", "method9"), typeMemberNames);
		}
	}

	@Test
	public void testBinaryOpStringsType() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/java/spoon/test/type/testclasses/Foo.java");
		CtModel model = launcher.buildModel();
		List<CtBinaryOperator> concats = model.getElements(new TypeFilter<>(CtBinaryOperator.class));
		concats.forEach(c -> assertEquals("java.lang.String", c.getType().toString()));
	}
}
