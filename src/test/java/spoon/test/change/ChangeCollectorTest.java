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
package spoon.test.change;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import spoon.support.modelobs.ChangeCollector;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.test.change.testclasses.SubjectOfChange;
import spoon.testing.utils.ModelUtils;

public class ChangeCollectorTest {

	@Test
	public void testChangeCollector() throws Exception {
		//contract: test ChangeCollector
		CtType<?> ctClass = ModelUtils.buildClass(SubjectOfChange.class);

		Factory f = ctClass.getFactory();

		assertNull(ChangeCollector.getChangeCollector(f.getEnvironment()));

		ChangeCollector changeCollector = new ChangeCollector().attachTo(f.getEnvironment());

		assertSame(changeCollector, ChangeCollector.getChangeCollector(f.getEnvironment()));

		//contract: after ChangeCollector is created there is no direct or indirect change
		assertEquals(0, changeCollector.getChanges(f.getModel().getRootPackage()).size());
		f.getModel().getRootPackage().filterChildren(null).forEach((CtElement e) -> {
			assertEquals(0, changeCollector.getDirectChanges(e).size());
		});

		ctClass.setSimpleName("aaa");

		assertEquals(new HashSet<>(Arrays.asList(CtRole.SUB_PACKAGE)), changeCollector.getChanges(f.getModel().getRootPackage()));
		assertEquals(new HashSet<>(), changeCollector.getDirectChanges(f.getModel().getRootPackage()));

		assertEquals(new HashSet<>(Arrays.asList(CtRole.CONTAINED_TYPE)), changeCollector.getChanges(ctClass.getPackage()));
		assertEquals(new HashSet<>(Arrays.asList()), changeCollector.getDirectChanges(ctClass.getPackage()));

		assertEquals(new HashSet<>(Arrays.asList(CtRole.NAME)), changeCollector.getChanges(ctClass));
		assertEquals(new HashSet<>(Arrays.asList(CtRole.NAME)), changeCollector.getDirectChanges(ctClass));

		CtField<?> field = ctClass.getField("someField");
		field.getDefaultExpression().delete();

		assertEquals(new HashSet<>(Arrays.asList(CtRole.NAME, CtRole.TYPE_MEMBER)), changeCollector.getChanges(ctClass));
		assertEquals(new HashSet<>(Arrays.asList(CtRole.NAME)), changeCollector.getDirectChanges(ctClass));

		assertEquals(new HashSet<>(Arrays.asList(CtRole.DEFAULT_EXPRESSION)), changeCollector.getChanges(field));
		assertEquals(new HashSet<>(Arrays.asList(CtRole.DEFAULT_EXPRESSION)), changeCollector.getDirectChanges(field));

		/*
		 * TODO:
		 * field.delete();
		 * calls internally setTypeMembers, which deletes everything and then adds remaining
		 */
		ctClass.removeTypeMember(field);

		assertEquals(new HashSet<>(Arrays.asList(CtRole.NAME, CtRole.TYPE_MEMBER)), changeCollector.getChanges(ctClass));
		assertEquals(new HashSet<>(Arrays.asList(CtRole.NAME, CtRole.TYPE_MEMBER)), changeCollector.getDirectChanges(ctClass));

		assertEquals(new HashSet<>(Arrays.asList(CtRole.DEFAULT_EXPRESSION)), changeCollector.getChanges(field));
		assertEquals(new HashSet<>(Arrays.asList(CtRole.DEFAULT_EXPRESSION)), changeCollector.getDirectChanges(field));
	}
}
