package spoon.reflect.code;

import spoon.reflect.declaration.CtCodeSnippet;
import spoon.support.compiler.SnippetCompilationError;

/**
 * This element is a code snippet that must represent a statement and can thus
 * be inserted in the program's model as is. Code snippets should be avoided
 * since no controls can be performed on them.
 */

public interface CtCodeSnippetStatement extends CtCodeSnippet, CtStatement {

	/**
	 * Compiles this statement code snippet to produce the corresponding AST
	 * statement.
	 *
	 * @param <S> the statement's type
	 * @return a statement
	 * @throws SnippetCompilationError
	 *             when the current snippet is not valid Java code
	 */
	<S extends CtStatement> S compile() throws SnippetCompilationError;

}
