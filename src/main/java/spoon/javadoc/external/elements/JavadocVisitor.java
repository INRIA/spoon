package spoon.javadoc.external.elements;

import spoon.javadoc.external.elements.snippets.JavadocSnippet;
import spoon.javadoc.external.references.JavadocReference;

public interface JavadocVisitor<T> {

	default T visitInlineTag(JavadocInlineTag tag) {
		for (JavadocElement element : tag.getElements()) {
			element.accept(this);
		}
		return null;
	}

	default T visitBlockTag(JavadocBlockTag tag) {
		for (JavadocElement element : tag.getElements()) {
			element.accept(this);
		}
		return null;
	}

	default T visitSnippet(JavadocSnippet snippet) {
		for (JavadocElement element : snippet.getElements()) {
			element.accept(this);
		}
		return null;
	}

	default T visitText(JavadocText text) {
		return null;
	}

	default T visitReference(JavadocReference reference) {
		return null;
	}
}
