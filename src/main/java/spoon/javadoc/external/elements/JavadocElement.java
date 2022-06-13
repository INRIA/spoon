package spoon.javadoc.external.elements;

/**
 * A semantic part of a javadoc comment.
 */
public interface JavadocElement {

	/**
	 * Accepts a javadoc visitor by calling the appropriate visit method.
	 *
	 * @param visitor the visitor to accept
	 * @param <T> the return type of the visitor
	 * @return the value returned by the visitor
	 */
	<T> T accept(JavadocVisitor<T> visitor);
}
