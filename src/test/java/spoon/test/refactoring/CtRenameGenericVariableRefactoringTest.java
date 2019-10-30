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
package spoon.test.refactoring;

import org.junit.Test;
import spoon.Launcher;
import spoon.refactoring.CtRenameGenericVariableRefactoring;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.*;

import static org.junit.Assert.assertNotEquals;

public class CtRenameGenericVariableRefactoringTest
{
	private static final String CLASS_STRING =
			"class TestClass { " +
					"int myFieldVar = 5; " +
					"private void foo(String inputWord) " +
					"{ int num = 5; return inputWord + (myFieldVar + num); }" +
			"}";

	/**
	 *  Tests that all types of variables in a class can be renamed by the Generic refactoring class
	 */
	@Test
	public void testRenameAllVariablesRandom() throws Exception {
		CtClass renamedClass = Launcher.parseClass(CLASS_STRING);

		// Randomly rename all the field variables in the class
		renamedClass.getElements(a -> a.getClass() == spoon.support.reflect.declaration.CtFieldImpl.class)
				.forEach(b -> {
					CtField field = (CtField) b;
					String originalName = field.getSimpleName();
					renameVar(field);
					// Assert that the name has changed
					assertNotEquals(originalName, field.getSimpleName());
				});

		// Now randomly rename all variables in each method
		for (Object ctMethod : renamedClass.getMethods()) {
			CtMethod method = (CtMethod) ctMethod;

			// Refactor the method parameters first
			method.getElements(a -> a.getClass() == spoon.support.reflect.declaration.CtParameterImpl.class)
					.forEach(b -> {
						CtParameter param = (CtParameter) b;
						String originalName = param.getSimpleName();
						renameVar(param);
						// Assert that the name has changed
						assertNotEquals(originalName, param.getSimpleName());
					});

			// Now refactor the method local variables
			method.getElements(a -> a.getClass() == spoon.support.reflect.code.CtLocalVariableImpl.class)
					.forEach(b -> {
						CtLocalVariable localVariable = (CtLocalVariable) b;
						String originalName = localVariable.getSimpleName();
						renameVar(localVariable);
						// Assert that the name has changed
						assertNotEquals(originalName, localVariable.getSimpleName());
					});
		}
	}

	private void renameVar(CtVariable variable) {
		String newName = randomVarName(10);
		new CtRenameGenericVariableRefactoring().setTarget(variable).setNewName(newName).refactor();
	}

	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static String randomVarName(int count) {
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}
}
