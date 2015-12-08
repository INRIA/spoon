/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.reflect.code;

import spoon.reflect.declaration.CtCodeSnippet;
import spoon.support.compiler.SnippetCompilationError;

/**
 * This element is a code snippet that must represent an expression and can thus
 * be inserted in the program's model as is. Code snippets should be avoided
 * since no controls can be performed on them.
 */
public interface CtCodeSnippetExpression<T> extends CtCodeSnippet, CtExpression<T> {

	/**
	 * Compiles this expression snippet to produce the corresponding AST expression.
	 *
	 * @throws SnippetCompilationError
	 * 		when the current snippet is not valid Java code expression
	 */
	<E extends CtExpression<T>> E compile() throws SnippetCompilationError;

}
