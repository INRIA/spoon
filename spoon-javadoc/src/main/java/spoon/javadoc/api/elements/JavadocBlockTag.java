/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.api.elements;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import spoon.javadoc.api.JavadocTagType;

/**
 * A javadoc block tag (like {@code @author} or {@code @see}.
 */
public class JavadocBlockTag implements JavadocElement {

	private final JavadocTagType tagType;
	private final List<JavadocElement> elements;

	/**
	 * @param elements the arguments and content
	 * @param tagType the type of the tag
	 */
	public JavadocBlockTag(List<JavadocElement> elements, JavadocTagType tagType) {
		this.tagType = tagType;
		this.elements = elements;
	}

	/**
	 * {@return the tag type (e.g. {@code @version}}
	 */
	public JavadocTagType getTagType() {
		return tagType;
	}

	/**
	 * {@return an unmodifiable view of the content of the tag. Potential arguments are the first element(s), content is
	 * the rest.}
	 */
	public List<JavadocElement> getElements() {
		return Collections.unmodifiableList(elements);
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
		return visitor.visitBlockTag(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		JavadocBlockTag that = (JavadocBlockTag) o;
		return Objects.equals(tagType, that.tagType) && Objects.equals(elements,
			that.elements);
	}

	@Override
	public int hashCode() {
		return Objects.hash(tagType, elements);
	}

	@Override
	public String toString() {
		return "JavadocBlockTag{"
		       + "tagType=" + tagType.getName()
		       + ", elements=" + elements
		       + '}';
	}
}
