/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.external.elements;

import spoon.javadoc.external.JavadocTagType;

import java.util.List;

/**
 * A javadoc inline tag (e.g. {@code @literal} or {@code @link}).
 */
public class JavadocInlineTag implements JavadocElement {
	private final List<JavadocElement> elements;
	private final JavadocTagType tagType;

	/**
	 * @param elements the arguments of the tag
	 * @param tagType the type of the tag
	 */
	public JavadocInlineTag(List<JavadocElement> elements, JavadocTagType tagType) {
		this.elements = elements;
		this.tagType = tagType;
	}

	/**
	 * @return the type of the tag
	 */
	public JavadocTagType getTagType() {
		return tagType;
	}

	/**
	 * @return the arguments of the tag
	 */
	public List<JavadocElement> getElements() {
		return elements;
	}

	@Override
	public <T> T accept(JavadocVisitor<T> visitor) {
		return visitor.visitInlineTag(this);
	}

	@Override
	public String toString() {
		return "JavadocInlineTag{"
			+ "elements=" + elements
			+ ", tagType=" + tagType.getName()
			+ '}';
	}
}
