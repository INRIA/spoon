package spoon.reflect.code;

import spoon.reflect.declaration.CtCodeSnippet;
import spoon.support.builder.CtSnippetCompilationError;

/**
 * This element is a code snippet that must represent a statement and can thus
 * be inserted in the program's model as is. Code snippets should be avoided
 * since no controls can be performed on them.
 */

public interface CtCodeSnippetStatement extends CtCodeSnippet, CtStatement {

	/**
	 * Compiles this statement code snippet to produce the corresponding AST statement.
	 * 
	 * @return a statement
	 * @throws CtSnippetCompilationError
	 *             when the current snippet is not valid Java code
	 */
	CtStatement compile() throws CtSnippetCompilationError;

}
