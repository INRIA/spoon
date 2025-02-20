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
package spoon.test.field;

import static org.junit.jupiter.api.Assertions.*;
import static spoon.testing.utils.ModelUtils.buildClass;
import static spoon.testing.utils.ModelUtils.createFactory;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;
import spoon.support.reflect.eval.VisitorPartialEvaluator;
import spoon.test.field.testclasses.A;
import spoon.test.field.testclasses.AddFieldAtTop;
import spoon.test.field.testclasses.BaseClass;
import spoon.testing.utils.LineSeparatorExtension;
import spoon.testing.utils.ModelTest;

public class FieldTest {

	@Test
	public void testAddAFieldInAClassAtAPositionGiven() {
		final Factory factory = createFactory();
		final CtClass<Object> fieldClass = factory.Class().create("FieldClass");

		final HashSet<ModifierKind> modifiers = new HashSet<>();
		modifiers.add(ModifierKind.STATIC);
		final CtField<Integer> first = createField(factory, modifiers, "FIELD");
		fieldClass.addField(first);

		final CtField<Integer> second = createField(factory, modifiers, "FIELD_2");
		second.setDefaultExpression(factory.Code().createCodeSnippetExpression(first.getSimpleName() + " + 1"));
		fieldClass.addField(1, second);

		final CtField<Integer> third = createField(factory, modifiers, "FIELD_3");
		third.setDefaultExpression(factory.Code().createCodeSnippetExpression(first.getSimpleName() + " + 1"));
		fieldClass.addField(1, third);

		assertEquals(3, fieldClass.getFields().size());
		assertEquals(first, fieldClass.getFields().get(0));
		assertEquals(third, fieldClass.getFields().get(1));
		assertEquals(second, fieldClass.getFields().get(2));
	}

	@Test
	public void testgetDeclaredFields() throws Exception {
		// contract: get*Fields works for both references
		final CtClass<AddFieldAtTop> aClass = (CtClass<AddFieldAtTop>) buildClass(AddFieldAtTop.class);

		assertEquals(1, aClass.getReference().getDeclaredFields().size());
		CtTypeReference<?> fileClass = aClass.getFactory().Type().get(File.class).getReference();
		assertEquals(13, fileClass.getDeclaredFields().size());
		assertEquals("pathSeparator", fileClass.getDeclaredField("pathSeparator").getSimpleName());
		assertEquals("pathSeparator", fileClass.getDeclaredOrInheritedField("pathSeparator").getSimpleName());

		// double check that we can still go to the declaration
		assertEquals("pathSeparator", fileClass.getDeclaredField("pathSeparator").getFieldDeclaration().getSimpleName());
	}

	@Test
	public void testAddFieldsAtTop() throws Exception {
		// contract: When we use CtType#addFieldAtTop, field added should be printed at the top of the type.
		final CtClass<AddFieldAtTop> aClass = (CtClass<AddFieldAtTop>) buildClass(AddFieldAtTop.class);

		assertEquals(1, aClass.getFields().size());

		final CtField<String> generated = aClass.getFactory().Field().create(null, new HashSet<>(), aClass.getFactory().Type().stringType(), "generated");
		aClass.addFieldAtTop(generated);
		final CtField<String> generated2 = aClass.getFactory().Field().create(null, new HashSet<>(), aClass.getFactory().Type().stringType(), "generated2");
		aClass.addFieldAtTop(generated2);

		assertEquals(3, aClass.getFields().size());
		// For now, DefaultJavaPrettyPrinter sorts elements according to their position.
		assertEquals(generated2, aClass.getTypeMembers().get(0));
		assertEquals(generated, aClass.getTypeMembers().get(1));
		assertEquals(aClass.getAnonymousExecutables().get(0), aClass.getTypeMembers().get(3));
	}

	@Test
	public void testFieldImplicitTarget() throws Exception {
		// contract: no "." when target is implicit
		final CtClass<AddFieldAtTop> aClass = (CtClass<AddFieldAtTop>) buildClass(AddFieldAtTop.class);

		List<CtFieldRead> fieldReads = aClass.getElements(new TypeFilter<>(CtFieldRead.class));
		assertEquals(1, fieldReads.size());
		assertEquals("i", fieldReads.get(0).toString());
		fieldReads.get(0).getTarget().setImplicit(false);
		assertFalse(fieldReads.get(0).getTarget().isImplicit());
		assertEquals("this.i", fieldReads.get(0).toString());
	}

	private CtField<Integer> createField(Factory factory, HashSet<ModifierKind> modifiers, String name) {
		final CtField<Integer> first = factory.Core().createField();
		first.setModifiers(modifiers);
		first.setType(factory.Type().integerPrimitiveType());
		first.setSimpleName(name);
		return first;
	}

	@ModelTest({
		"./src/test/java/spoon/test/field/testclasses/A.java",
		"./src/test/java/spoon/test/field/testclasses/BaseClass.java",
	})
	public void testGetDefaultExpression(Factory factory) {
		final CtClass<A> aClass = factory.Class().get(A.class);

		// contract: isPartOfJointDeclaration works per the specification in the javadoc
		assertEquals(false,aClass.getField("alone1").isPartOfJointDeclaration());
		assertEquals(false,aClass.getField("alone2").isPartOfJointDeclaration());
		assertEquals(false,aClass.getField("alone3").isPartOfJointDeclaration());
		assertEquals(true,aClass.getField("i").isPartOfJointDeclaration());
		assertEquals(true,aClass.getField("k").isPartOfJointDeclaration());
		assertEquals(true,aClass.getField("n").isPartOfJointDeclaration());
		assertEquals(true,aClass.getField("l").isPartOfJointDeclaration());
		assertEquals(true,aClass.getField("m").isPartOfJointDeclaration());

		// bonus assertions for Java noobs
		assertEquals(0,A.l); // default initialization of Java
		assertEquals(1,A.m);

		CtClass<A.ClassB> bClass = aClass.getFactory().Class().get(A.ClassB.class);
		List<CtMethod<?>> methods = bClass.getMethodsByName("getKey");

		assertEquals(1, methods.size());

		CtReturn<?> returnExpression = methods.get(0).getBody().getStatement(0);

		CtFieldRead fieldRead = (CtFieldRead) returnExpression.getReturnedExpression();

		assertEquals("spoon.test.field.testclasses.BaseClass.PREFIX", fieldRead.toString());

		CtField<?> field = fieldRead.getVariable().getDeclaration();

		CtClass<BaseClass> baseClass = aClass.getFactory().Class().get(BaseClass.class);
		CtField<?> expectedField = baseClass.getField("PREFIX");

		assertEquals(expectedField, field);

		VisitorPartialEvaluator visitorPartial = new VisitorPartialEvaluator();

		Object retour = visitorPartial.evaluate(methods.get(0));

		assertNotNull(retour);
	}

	@ModelTest("./src/test/resources/spoon/test/noclasspath/fields/Toto.java")
	public void getFQNofFieldReference(CtModel model) {
		// contract: when a reference field origin cannot be determined a call to its qualified name returns an explicit value
		List<CtFieldReference> elements = model.getElements(new TypeFilter<>(CtFieldReference.class));
		assertEquals(1, elements.size());

		CtFieldReference fieldReference = elements.get(0);
		assertEquals("field", fieldReference.getSimpleName());
		assertEquals("<unknown>#field", fieldReference.getQualifiedName());
	}

	@Test
	@ExtendWith(LineSeparatorExtension.class)
	public void bugAfterRefactoringImports() {
		Launcher launcher = new Launcher();
		Factory factory = launcher.getFactory();
		final CtClass<?> klass = factory.createClass("foo.A");

		final CtFieldRead<Object> fieldRead = factory.createFieldRead();

		final CtField<Object> negative_infinity = (CtField<Object>) factory.Class().get(Double.class).getField("NEGATIVE_INFINITY");
		fieldRead.setVariable(negative_infinity.getReference());

		launcher.getEnvironment().setAutoImports(false);
		assertEquals("java.lang.Double.NEGATIVE_INFINITY", fieldRead.toString());

		launcher.getEnvironment().setAutoImports(true);
		assertEquals("Double.NEGATIVE_INFINITY", fieldRead.toString());

		final CtField<Object> field = (CtField<Object>) factory.Class().get(File.class).getField("separator");
		fieldRead.setVariable(field.getReference());
		field.setDefaultExpression(fieldRead);
		klass.addField(field);

		launcher.getEnvironment().setAutoImports(true);
		assertEquals("package foo;\n" +
				"import java.io.File;\n" +
				"class A {\n" +
				"    public static final String separator = File.separator;\n" +
				"}\n", klass.toStringWithImports());

	}

	@ModelTest(
					"./src/test/java/spoon/test/field/testclasses/AnnoWithConst.java"
	)
	void testGetActualFieldForConstantInAnnotation(CtModel ctModel) {
		// contract: CtFieldReference#getActualField() returns the field for constants in annotations
		CtFieldReference<?> access = ctModel.getElements(new TypeFilter<CtFieldReference<?>>(CtFieldReference.class))
						.stream()
						.filter(field -> field.getSimpleName().equals("VALUE"))
						.findFirst()
						.orElseGet(() -> fail("No reference to VALUE found"));
		assertNotNull(assertDoesNotThrow(access::getActualField));
	}

	@Test
	void testArrayLengthDeclaringType() {
		// contract: the "length" field of arrays has a proper declaring type
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile("public class Example {\n" +
																							"    static final String[] field;\n" +
																							"    public static void main(String[] args) {\n" +
																							"        int i = args.length;\n" +
																							"        int j = field.length;\n" +
																							"    }\n" +
																							"}\n"));
		CtModel ctModel = launcher.buildModel();
		List<CtFieldReference<?>> elements = ctModel.getElements(new TypeFilter<CtFieldReference<?>>(CtFieldReference.class))
						.stream()
						.filter(field -> field.getSimpleName().equals("length"))
						.collect(Collectors.toList());
		CtType<?> component = launcher.getFactory().Type().get(String.class);
		CtTypeReference<?> arrayType = launcher.getFactory().Type().createArrayReference(component);

		assertEquals(2, elements.size(), "Unexpected number of .length references");

		assertEquals(arrayType, elements.get(0).getDeclaringType());
		assertEquals(arrayType, elements.get(1).getDeclaringType());
	}

	@Test
	void testArrayLengthModifiers() {
		// contract: the "length" field in arrays has exactly the modifiers "public" and "final"
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile("public class Example {\n" +
						"    public static void main(String[] args) {\n" +
						"        int i = args.length;\n" +
						"    }\n" +
						"}\n"));
		CtModel ctModel = launcher.buildModel();
		List<CtFieldReference<?>> elements = ctModel.getElements(new TypeFilter<>(CtFieldReference.class));
		assertEquals(1, elements.size());
		assertEquals(Set.of(ModifierKind.PUBLIC, ModifierKind.FINAL), elements.get(0).getModifiers());
	}

	@Test
	void testArrayLengthDeclaringTypeNested() {
		// contract: the declaring type of a "length" access on arrays is set even when nested
		Launcher launcher = new Launcher();
		launcher.addInputResource(new VirtualFile(
						"public class Example {\n" +
						"	public String[] array = new String[4];\n" +
						"	public static void main(String[] args) {\n" +
						"		Example other = new Example();\n" +
						"		int i = other.array.length;\n" +
						"	}\n" +
						"}"
		));

		CtModel ctModel = launcher.buildModel();
		List<CtFieldReference<?>> elements = ctModel.getElements(new TypeFilter<>(CtFieldReference.class));
		assertEquals(2, elements.size());
		CtArrayTypeReference<?> stringArrayRef = launcher.getFactory().createArrayReference("java.lang.String");
		assertEquals(stringArrayRef, elements.get(1).getDeclaringType());
	}

}
