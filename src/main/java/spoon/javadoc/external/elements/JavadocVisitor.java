package spoon.javadoc.external.elements;

import spoon.javadoc.external.elements.snippets.JavadocSnippetTag;

/**
 * A visitor for javadoc elements.
 *
 * @param <T> the return type of the visit methods
 */
public interface JavadocVisitor<T> {

	/**
	 * @param tag the inline tag to visit
	 * @return a return value
	 * @implNote the default implementation always returns null and visits all arguments
	 */
	default T visitInlineTag(JavadocInlineTag tag) {
		for (JavadocElement element : tag.getElements()) {
			element.accept(this);
		}
		return null;
	}

	/**
	 * @param tag the block tag to visit
	 * @return a return value
	 * @implNote the default implementation always returns null and visits all elements
	 */
	default T visitBlockTag(JavadocBlockTag tag) {
		for (JavadocElement element : tag.getElements()) {
			element.accept(this);
		}
		return null;
	}

	/**
	 * @param snippet the snippet tag to visit
	 * @return a return value
	 * @implNote the default implementation always returns null and visits all elements
	 */
	default T visitSnippet(JavadocSnippetTag snippet) {
		for (JavadocElement element : snippet.getElements()) {
			element.accept(this);
		}
		return null;
	}

	/**
	 * @param text the javadoc text to visit
	 * @return a return value
	 * @implNote the default implementation always returns null
	 */
	default T visitText(JavadocText text) {
		return null;
	}

	/**
	 * @param reference the javadoc reference to visit
	 * @return a return value
	 * @implNote the default implementation always returns null
	 */
	default T visitReference(JavadocReference reference) {
		return null;
	}
}
