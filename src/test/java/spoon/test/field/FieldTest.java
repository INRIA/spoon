/*
 * Copyright (C) 2006-2015 INRIA and contributors
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
import static spoon.testing.Assert.assertThat;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.buildClass;
import static spoon.testing.utils.ModelUtils.createFactory;

import java.io.File;
import java.util.HashSet;

import org.junit.Test;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.test.field.testclasses.AddFieldAtTop;

public class FieldTest {
	@Test
	public void testAddAFieldInAClassAtAPositionGiven() throws Exception {
		final Factory factory = createFactory();
		final CtClass<Object> fieldClass = factory.Class().create("FieldClass");

		final HashSet<ModifierKind> modifiers = new HashSet<ModifierKind>();
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

		assertThat(aClass).isEqualTo(build(new File("./src/test/resources/expected/AddFieldAtTop.java")).Type().get("AddFieldAtTop"));
	}

	private CtField<Integer> createField(Factory factory, HashSet<ModifierKind> modifiers, String name) {
		final CtField<Integer> first = factory.Core().createField();
		first.setModifiers(modifiers);
		first.setType(factory.Type().INTEGER_PRIMITIVE);
		first.setSimpleName(name);
		return first;
	}
}
