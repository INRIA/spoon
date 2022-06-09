package spoon.javadoc.external.elements;

import spoon.javadoc.external.elements.snippets.JavadocSnippet;
import spoon.javadoc.external.references.JavadocReference;

public interface JavadocVisitor {

	default void visitInlineTag(JavadocInlineTag tag) {
		for (JavadocElement element : tag.getElements()) {
			element.accept(this);
		}
	}

	default void visitBlockTag(JavadocBlockTag tag) {
		for (JavadocElement element : tag.getElements()) {
			element.accept(this);
		}
	}

	default void visitSnippet(JavadocSnippet snippet) {
		for (JavadocElement element : snippet.getElements()) {
			element.accept(this);
		}
	}

	default void visitText(JavadocText text) {
	}

	default void visitReference(JavadocReference reference) {
	}
}
