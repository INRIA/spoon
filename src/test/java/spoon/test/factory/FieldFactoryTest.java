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
package spoon.test.factory;

import org.junit.Test;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FieldFactory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.test.targeted.testclasses.Bar;
import spoon.test.targeted.testclasses.Foo;
import spoon.test.targeted.testclasses.SuperClass;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

public class FieldFactoryTest {

	@Test
	public void testCreate() throws Exception {

		CtClass<?> type = build("spoon.test.testclasses", "SampleClass");
		FieldFactory ff = type.getFactory().Field();
		TypeFactory tf = type.getFactory().Type();

		Set<ModifierKind> mods = new HashSet<>();
		mods.add(ModifierKind.PRIVATE);
		CtTypeReference<?> tref = tf.createReference(String.class);
		ff.create(type,mods,tref,"name");

		CtField<?> field = type.getField("name");
		assertEquals("name", field.getSimpleName());
		assertEquals(tref, field.getType());

		CtElement parent = field.getParent();
		assertTrue(parent instanceof CtClass<?>);
		assertEquals("SampleClass", ((CtClass<?>)parent).getSimpleName());
	}

	@Test
	public void testCreateFromSource() throws Exception {

		CtClass<?> target = build("spoon.test.testclasses", "SampleClass");
		Factory factory = build(Foo.class, Bar.class, SuperClass.class);
		final CtClass<Object> type = factory.Class().get(Foo.class);
		CtField<?> source = type.getField("i");
		FieldFactory ff = type.getFactory().Field();
		TypeFactory tf = type.getFactory().Type();

		ff.create(target,source);

		CtField<?> field = target.getField("i");
		assertEquals("i", field.getSimpleName());
		CtTypeReference<?> tref = tf.createReference("int");
		assertEquals(tref, field.getType());

		CtElement parent = field.getParent();
		assertTrue(parent instanceof CtClass<?>);
		assertEquals("SampleClass", ((CtClass<?>)parent).getSimpleName());
	}
}
