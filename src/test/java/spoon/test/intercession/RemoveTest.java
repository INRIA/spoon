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
package spoon.test.intercession;

import static org.junit.Assert.assertEquals;
import static spoon.testing.utils.ModelUtils.createFactory;

import java.util.ArrayList;

import org.junit.Test;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;

public class RemoveTest {

	@Test
	public void testRemoveAllStatements() {
		Factory factory = createFactory();
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {" + " int x=0;int y=0;"
								+ "}};")
				.compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		CtBlock<?> body = foo.getBody();

		assertEquals(2, body.getStatements().size());

		//iterate on copy of list of statements, otherwise it fails with concurrent modification exception
		for (CtStatement s : new ArrayList<>(body.getStatements())) {
			body.removeStatement(s);
		}

		assertEquals(0, body.getStatements().size());
	}
}
