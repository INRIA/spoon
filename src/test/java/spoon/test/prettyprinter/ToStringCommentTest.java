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
 * knowledge of the CeCILL-C license and you accept the terms.
 */
package spoon.test.prettyprinter;

import spoon.Launcher;
import spoon.reflect.declaration.CtType;
import spoon.testing.utils.ModelTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ToStringCommentTest {

	@ModelTest("./src/test/resources/spoon/test/prettyprinter/toStringCommentTest")
	public void testToStringWithComplexComments(Launcher launcher) {
		// contract: toString() should not crash when processing complex comments
		// This is a regression test for issue #3382 where cleanComment method could crash
		var types = launcher.getFactory().Class().getAll();
		assertThat(types).withFailMessage("Should load test resources").isNotEmpty();

		for (CtType<?> type : types) {
			assertThat(type.toString()).withFailMessage("toString() should not return null for type: " + type.getQualifiedName()).isNotNull();
		}
	}
}
