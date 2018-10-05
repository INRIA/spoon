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
package spoon.reflect.declaration;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.CtScanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Marcel Steinbeck
 */
public class UnknownDeclarationTest {

	private static class ExecutableReferenceVisitor extends CtScanner {

		int referenceCounter = 0;

		@Override
		public <T> void visitCtExecutableReference(final CtExecutableReference<T> reference) {
			final CtExecutable executable = reference.getDeclaration();
			assertNull(executable);

			referenceCounter++;
		}
	}

	@Test
	public void testUnknownCalls() {
		final Launcher runLaunch = new Launcher();
		runLaunch.getEnvironment().setNoClasspath(true);
		runLaunch.addInputResource("./src/test/resources/noclasspath/UnknownCalls.java");
		runLaunch.buildModel();

		final CtPackage rootPackage = runLaunch.getFactory().Package().getRootPackage();
		final ExecutableReferenceVisitor visitor = new ExecutableReferenceVisitor();
		visitor.scan(rootPackage);
		// super constructor to Object +
		// UnknownClass constructor +
		// UnknownClass method
		assertEquals(3, visitor.referenceCounter);
	}
}
