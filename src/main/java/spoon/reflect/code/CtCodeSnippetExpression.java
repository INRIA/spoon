/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.declaration.CtCodeSnippet;
import spoon.support.compiler.SnippetCompilationError;

/**
 * This element is a code snippet that must represent an expression and can thus
 * be inserted in the program's model as is. Code snippets should be avoided
 * since no controls can be performed on them.
 */
public interface CtCodeSnippetExpression<T> extends CtExpression<T>, CtCodeSnippet {

	/**
	 * Compiles this expression snippet to produce the corresponding AST expression.
	 *
	 * @throws SnippetCompilationError
	 * 		when the current snippet is not valid Java code expression
	 */
	<E extends CtExpression<T>> E compile() throws SnippetCompilationError;

	@Override
	CtCodeSnippetExpression<T> clone();

}
