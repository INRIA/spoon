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
package spoon.test.model;

import org.junit.Test;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static spoon.testing.utils.ModelUtils.build;

public class AnonymousExecutableTest {

	@Test
	public void testStatements() throws Exception {
		CtType<?> type = build("spoon.test.model.testclasses", "AnonymousExecutableClass");
		CtAnonymousExecutable anonexec =
			type.
			getElements(new TypeFilter<>(CtAnonymousExecutable.class)).
			get(0);
		List<CtStatement> stats = anonexec.getBody().getStatements();
		assertEquals(1, stats.size());
	}
}
