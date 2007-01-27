package spoon.reflect.code;

/**
 * This element is a code snippet that must represent an expression and can thus
 * be inserted in the program's model as is. Code snippets should be avoided
 * since no controls can be performed on them.
 */
public interface CtCodeSnippetExpression<T> extends CtCodeSnippet,
		CtExpression<T> {
}
