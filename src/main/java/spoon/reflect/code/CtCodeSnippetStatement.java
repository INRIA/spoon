package spoon.reflect.code;

/**
 * This element is a code snippet that must represent a statement and can thus
 * be inserted in the program's model as is. Code snippets should be avoided since no
 * controls can be performed on them.
 */

public interface CtCodeSnippetStatement extends CtCodeSnippet, CtStatement {
}
