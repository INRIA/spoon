/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.api.elements;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import spoon.javadoc.api.JavadocTagType;

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

	/**
	 * Returns the (first) argument of this block tag, if it is of the given type.
	 *
	 * @param type the type you expect the argument to be
	 * @param <T> the type of the argument
	 * @return the argument, if it exists and is of the given type
	 */
	public <T extends JavadocElement> Optional<T> getArgument(Class<T> type) {
		if (getElements().isEmpty()) {
			return Optional.empty();
		}
		JavadocElement element = getElements().get(0);
		if (type.isInstance(element)) {
			return Optional.of(type.cast(element));
		}
		return Optional.empty();
	}

	@Override
	public <T> T accept(JavadocVisitor<T> visitor) {
		return visitor.visitInlineTag(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		JavadocInlineTag that = (JavadocInlineTag) o;
		return Objects.equals(elements, that.elements) && Objects.equals(tagType,
			that.tagType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(elements, tagType);
	}

	@Override
	public String toString() {
		return "JavadocInlineTag{"
		       + "elements=" + elements
		       + ", tagType=" + tagType.getName()
		       + '}';
	}
}
