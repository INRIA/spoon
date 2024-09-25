/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.api.elements;

import spoon.javadoc.api.elements.snippets.JavadocSnippetTag;

/**
 * A visitor for javadoc elements.
 *
 * @param <T> the return type of the visit methods
 */
public interface JavadocVisitor<T> {

	/**
	 * @return the default value to return if a visit method is not overwritten
	 */
	T defaultValue();

	/**
	 * @param tag the inline tag to visit
	 * @return a return value
	 * @implNote the default implementation visits all arguments
	 */
	default T visitInlineTag(JavadocInlineTag tag) {
		for (JavadocElement element : tag.getElements()) {
			element.accept(this);
		}
		return defaultValue();
	}

	/**
	 * @param tag the block tag to visit
	 * @return a return value
	 * @implNote the default implementation visits all elements
	 */
	default T visitBlockTag(JavadocBlockTag tag) {
		for (JavadocElement element : tag.getElements()) {
			element.accept(this);
		}
		return defaultValue();
	}

	/**
	 * @param snippet the snippet tag to visit
	 * @return a return value
	 * @implNote the default implementation visits all elements
	 */
	default T visitSnippet(JavadocSnippetTag snippet) {
		for (JavadocElement element : snippet.getElements()) {
			element.accept(this);
		}
		return defaultValue();
	}

	/**
	 * @param text the javadoc text to visit
	 * @return a return value
	 */
	default T visitText(JavadocText text) {
		return defaultValue();
	}

	/**
	 * @param reference the javadoc reference to visit
	 * @return a return value
	 */
	default T visitReference(JavadocReference reference) {
		return defaultValue();
	}
}
