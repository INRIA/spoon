package spoon.javadoc.external.elements;

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

	default void visitText(JavadocText text) {
	}

	default void visitReference(JavadocReference reference) {
	}

}
