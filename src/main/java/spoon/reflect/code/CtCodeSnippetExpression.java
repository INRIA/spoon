package spoon.reflect.code;

import spoon.reflect.declaration.CtCodeSnippet;
import spoon.support.builder.CtSnippetCompilationError;

/**
 * This element is a code snippet that must represent an expression and can thus
 * be inserted in the program's model as is. Code snippets should be avoided
 * since no controls can be performed on them.
 */
public interface CtCodeSnippetExpression<T> extends CtCodeSnippet,
		CtExpression<T> {

	/**
	 * Compiles this expression snippet to produce the corresponding AST expression.
	 * 
	 * @throws CtSnippetCompilationError
	 *             when the current snippet is not valid Java code expression
	 */
	CtExpression<T> compile() throws CtSnippetCompilationError;

}
