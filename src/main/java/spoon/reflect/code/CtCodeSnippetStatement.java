/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.declaration.CtCodeSnippet;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.compiler.SnippetCompilationError;

/**
 * This element is a code snippet that must represent a statement and can thus
 * be inserted in the program's model as is. Code snippets should be avoided
 * since no controls can be performed on them.
 */

public interface CtCodeSnippetStatement extends CtStatement, CtCodeSnippet {

	/**
	 * Compiles this statement code snippet to produce the corresponding AST
	 * statement.
	 *
	 * If you want to compile a non-void return or a snippet that uses a non-void  return,
	 * use, {@link spoon.support.compiler.SnippetCompilationHelper#compileStatement(CtCodeSnippetStatement, CtTypeReference)}
	 *
	 * @return a statement
	 * @throws SnippetCompilationError
	 * 		when the current snippet is not valid Java code
	 */
	<S extends CtStatement> S compile() throws SnippetCompilationError;

	@Override
	CtCodeSnippetStatement clone();
}
