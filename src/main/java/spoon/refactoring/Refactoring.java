/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.refactoring;

import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

/**
 * Contains all methods to refactor code elements in the AST.
 */
public final class Refactoring {
	/**
	 * Changes name of a type element.
	 *
	 * @param type
	 * 		Type in the AST.
	 * @param name
	 * 		New name of the element.
	 */
	public static void changeTypeName(final CtType<?> type, String name) {

		final String typeQFN = type.getQualifiedName();

		final List<CtTypeReference<?>> references = Query.getElements(type.getFactory(), new TypeFilter<CtTypeReference<?>>(CtTypeReference.class) {
			@Override
			public boolean matches(CtTypeReference<?> reference) {
				String refFQN = reference.getQualifiedName();
				return typeQFN.equals(refFQN);
			}
		});

		type.setSimpleName(name);
		for (CtTypeReference<?> reference : references) {
			reference.setSimpleName(name);
		}
	}

	/**
	 * Changes name of a {@link CtLocalVariable}.
	 *
	 * @param localVariable
	 * 		to be renamed {@link CtLocalVariable} in the AST.
	 * @param newName
	 * 		New name of the element.
	 * @throws RefactoringException when rename to newName would cause model inconsistency, like ambiguity, shadowing of other variables, etc.
	 */
	public static void changeLocalVariableName(CtLocalVariable<?> localVariable, String newName) throws RefactoringException {
		new CtRenameLocalVariableRefactoring().setTarget(localVariable).setNewName(newName).refactor();
	}
}
