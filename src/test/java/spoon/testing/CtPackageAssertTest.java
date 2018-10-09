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
package spoon.testing;

import org.junit.Test;

import spoon.SpoonException;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static spoon.testing.Assert.assertThat;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.createFactory;

public class CtPackageAssertTest {

	@Test
	public void testEqualityBetweenTwoCtPackage() {
		//contract: two packages, one made by test code, second made by compilation from sources are equal
		final Factory factory = createFactory();
		final CtPackage aRootPackage = factory.Package().getOrCreate("");
		List<CtType<?>> types1 = aRootPackage.filterChildren(new TypeFilter<>(CtClass.class)).list();
		assertEquals(0, types1.size());
		factory.Class().create("spoon.testing.testclasses.Foo").addModifier(ModifierKind.PUBLIC);
		factory.Class().create("spoon.testing.testclasses.Bar").addModifier(ModifierKind.PUBLIC);
		List<CtType<?>> types2 = aRootPackage.filterChildren(new TypeFilter<>(CtClass.class)).list();
		assertEquals(2, types2.size());
		final CtPackage aRootPackage2 = build(new File("./src/test/java/spoon/testing/testclasses/")).Package().getRootPackage();
		assertNotSame(aRootPackage, aRootPackage2);
		assertThat(aRootPackage2).isEqualTo(aRootPackage);
	}

	@Test(expected = AssertionError.class)
	public void testEqualityBetweenTwoDifferentCtPackage() {
		assertThat(build(new File("./src/test/java/spoon/testing/testclasses/")).Package().getRootPackage()).isEqualTo(createFactory().Package().getOrCreate("another.package"));
	}

	@Test(expected = AssertionError.class)
	public void testEqualityBetweenTwoCtPackageWithDifferentTypes() {
		final Factory factory = createFactory();
		final CtPackage aRootPackage = factory.Package().getOrCreate("");
		factory.Class().create("spoon.testing.testclasses.Foo").addModifier(ModifierKind.PUBLIC);
		assertThat(build(new File("./src/test/java/spoon/testing/testclasses/")).Package().getRootPackage()).isEqualTo(aRootPackage);
	}

	@Test
	public void testAddTypeToPackage() {
		final Factory factory = createFactory();
		final CtType<?> type = factory.Core().createClass();
		type.setSimpleName("X");
		//contract: type is created in the root package
		assertSame(factory.getModel().getRootPackage(), type.getPackage());
		assertEquals("X", type.getQualifiedName());
		final CtPackage aPackage1= factory.Package().getOrCreate("some.package");
		//contract: type can be moved from root package to any other package
		aPackage1.addType(type);
		assertEquals("some.package.X", type.getQualifiedName());
		//contract: second add of type into same package does nothing
		aPackage1.addType(type);
		assertEquals("some.package.X", type.getQualifiedName());
		final CtPackage aPackage2= factory.Package().getOrCreate("another.package");
		try {
			//contract: type cannot be moved to another package as long as it belongs to some not root package
			aPackage2.addType(type);
			fail();
		} catch (SpoonException e) {
			//OK
		}
		assertEquals("some.package.X", type.getQualifiedName());
		//contract: type can be moved to another package after it is removed from current package
		type.getPackage().removeType(type);
		aPackage2.addType(type);
		assertEquals("another.package.X", type.getQualifiedName());
	}
}
