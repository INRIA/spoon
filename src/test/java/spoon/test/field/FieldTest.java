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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static spoon.testing.utils.ModelUtils.buildClass;
import static spoon.testing.utils.ModelUtils.createFactory;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.eval.VisitorPartialEvaluator;
import spoon.test.field.testclasses.A;
import spoon.test.field.testclasses.AddFieldAtTop;
import spoon.test.field.testclasses.BaseClass;

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

		final CtField<String> generated = aClass.getFactory().Field().create(null, new HashSet<>(), aClass.getFactory().Type().STRING, "generated");
		aClass.addFieldAtTop(generated);
		final CtField<String> generated2 = aClass.getFactory().Field().create(null, new HashSet<>(), aClass.getFactory().Type().STRING, "generated2");
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
		first.setType(factory.Type().INTEGER_PRIMITIVE);
		first.setSimpleName(name);
		return first;
	}

	@Test
	public void testGetDefaultExpression() {
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/field/testclasses/A.java");
		spoon.addInputResource("./src/test/java/spoon/test/field/testclasses/BaseClass.java");
		spoon.buildModel();

		final CtClass<A> aClass = spoon.getFactory().Class().get(A.class);

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

	@Test
	public void getFQNofFieldReference() {
		// contract: when a reference field origin cannot be determined a call to its qualified name returns an explicit value
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/spoon/test/noclasspath/fields/Toto.java");
		launcher.getEnvironment().setNoClasspath(true);
		CtModel ctModel = launcher.buildModel();
		List<CtFieldReference> elements = ctModel.getElements(new TypeFilter<>(CtFieldReference.class));
		assertEquals(1, elements.size());

		CtFieldReference fieldReference = elements.get(0);
		assertEquals("field", fieldReference.getSimpleName());
		assertEquals("<unknown>#field", fieldReference.getQualifiedName());
	}

	@Test
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
				"}", klass.toStringWithImports());

	}


}
