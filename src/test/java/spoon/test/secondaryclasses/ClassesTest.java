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
package spoon.test.secondaryclasses;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.buildClass;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.junit.Test;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.comparator.CtLineElementComparator;
import spoon.test.secondaryclasses.testclasses.AnonymousClass.I;
import spoon.test.secondaryclasses.testclasses.ClassWithInternalPublicClassOrInterf;
import spoon.test.secondaryclasses.testclasses.Pozole;
import spoon.test.secondaryclasses.testclasses.PrivateInnerClasses;

public class ClassesTest {

	@Test
	public void testClassWithInternalPublicClassOrInterf() throws Exception {
		CtClass<?> type = build("spoon.test.secondaryclasses.testclasses",
				"ClassWithInternalPublicClassOrInterf");
		assertEquals("ClassWithInternalPublicClassOrInterf",
				type.getSimpleName());
		assertEquals(3,
				type.getElements(new TypeFilter<CtType<?>>(CtType.class))
						.size());
		assertEquals(2, type.getNestedTypes().size());
		assertTrue(type
				.getNestedTypes()
				.contains(
						(type.getFactory().Class()
								.get(ClassWithInternalPublicClassOrInterf.InternalClass.class))));
		assertEquals(
				1,
				type.getElements(
						new NamedElementFilter<>(CtNamedElement.class,"InternalInterf"))
						.size());
	}

	@Test
	public void testAnonymousClass() throws Exception {
		CtClass<?> type = build("spoon.test.secondaryclasses.testclasses", "AnonymousClass");
		assertEquals("AnonymousClass", type.getSimpleName());

		CtNewClass<?> x = type.getElements(
				new TypeFilter<CtNewClass<?>>(CtNewClass.class)).get(0);
		CtNewClass<?> y = type.getElements(
				new TypeFilter<CtNewClass<?>>(CtNewClass.class)).get(1);

		if (x.getParent() instanceof CtBlock) {
			CtNewClass<?> z = x;
			x = y;
			y = z;
		}

		// names of anonymous classes
		// classes should always have different names
		CtClass<?> anonymousClass0 = x.getAnonymousClass();
		CtClass<?> anonymousClass1 = y.getAnonymousClass();

		assertEquals("1",anonymousClass0.getSimpleName());
		assertEquals("2",anonymousClass1.getSimpleName());

		assertEquals("spoon.test.secondaryclasses.testclasses.AnonymousClass$1",anonymousClass0.getQualifiedName());
		assertEquals("spoon.test.secondaryclasses.testclasses.AnonymousClass$2",anonymousClass1.getQualifiedName());

		// ActionListener is not in the Spoon path but we can build a shadow element.
		assertNull(x.getType().getDeclaration());
		assertNotNull(x.getType().getTypeDeclaration());

		// but the actual class is known
		assertSame(ActionListener.class, x.getType().getActualClass());

		assertNotNull(y.getType().getDeclaration());

		assertEquals("spoon.test.secondaryclasses.testclasses.AnonymousClass$2()", y.getExecutable().toString());

		assertEquals(type.getFactory().Type().createReference(I.class), y.getAnonymousClass().getSuperInterfaces().toArray(new CtTypeReference[0])[0]);

	}

	@Test
	public void testIsAnonymousMethodInCtClass() throws Exception {
		CtClass<?> type = build("spoon.test.secondaryclasses.testclasses", "AnonymousClass");

		TreeSet<CtClass<?>> ts = new TreeSet<>(new CtLineElementComparator());
		ts.addAll(type.getElements(new AbstractFilter<CtClass<?>>(CtClass.class) {
			@Override
			public boolean matches(CtClass<?> element) {
				return element.isAnonymous();
			}
		}));
		List<CtClass<?>> anonymousClass = new ArrayList<>();
		anonymousClass.addAll(ts);
		assertFalse(type.isAnonymous());
		assertTrue(ts.first().isAnonymous());
		assertTrue(anonymousClass.get(1).isAnonymous());
		assertEquals(2, anonymousClass.size());
		assertEquals(2, ts.size());

		assertEquals("spoon.test.secondaryclasses.testclasses.AnonymousClass$1", anonymousClass.get(0).getQualifiedName());
		assertEquals("spoon.test.secondaryclasses.testclasses.AnonymousClass$2", anonymousClass.get(1).getQualifiedName());
	}

	@Test
	public void testTopLevel() throws Exception {
		CtClass<?> type = build("spoon.test.secondaryclasses.testclasses", "TopLevel");
		assertEquals("TopLevel", type.getSimpleName());

		CtClass<?> x = type.getElements(
				new NamedElementFilter<>(CtClass.class,"InnerClass")).get(0);
		List<CtField<?>> fields = x.getFields();
		assertEquals(1, fields.size());
		assertEquals(1, fields.get(0).getType().getActualTypeArguments().size());
		assertEquals("?",
				fields.get(0).getType().getActualTypeArguments().get(0)
						.getSimpleName());
	}

	@Test
	public void testInnerClassContruction() throws Exception {
		Factory f = build(PrivateInnerClasses.class);
		CtClass<?> c = f.Class().get(PrivateInnerClasses.class);
		assertNotNull(c);
		assertEquals(0, f.getEnvironment().getErrorCount());
	}

	@Test
	public void testAnonymousClassInStaticField() throws Exception {
		final CtType<Pozole> type = buildClass(Pozole.class);

		final CtNewClass<?> anonymousClass = type.getField("CONFLICT_HOOK").getElements(new TypeFilter<>(CtNewClass.class)).get(1);
		final CtVariableRead<?> ctVariableRead = anonymousClass.getElements(new TypeFilter<>(CtVariableRead.class)).get(2);
		final CtVariable<?> declaration = ctVariableRead.getVariable().getDeclaration();

		assertNotNull(declaration);
		assertEquals("int i", declaration.toString());
	}
}
