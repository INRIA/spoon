/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.api.elements.snippets;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import spoon.javadoc.api.StandardJavadocTagType;
import spoon.javadoc.api.elements.JavadocInlineTag;
import spoon.javadoc.api.elements.JavadocText;
import spoon.javadoc.api.elements.JavadocVisitor;

/**
 * An {@code @snippet} inline tag.
 * <p>
 * This class also contains the attributes of the tag and the body as its first element. The body can be parsed using
 * the {@link spoon.javadoc.api.parsing.SnippetFileParser}.
 */
public class JavadocSnippetTag extends JavadocInlineTag {

	private final Map<String, String> attributes;

	/**
	 * Creates a new snippet inline tag with the given body and attributes.
	 *
	 * @param body the body of the snippet
	 * @param attributes the attributes of the snippet
	 */
	public JavadocSnippetTag(JavadocText body, Map<String, String> attributes) {
		super(List.of(body), StandardJavadocTagType.SNIPPET);

		this.attributes = attributes;
	}

	/**
	 * @return the snippet attributes
	 */
	public Map<String, String> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		JavadocSnippetTag that = (JavadocSnippetTag) o;
		return Objects.equals(attributes, that.attributes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), attributes);
	}

	@Override
	public <T> T accept(JavadocVisitor<T> visitor) {
		return visitor.visitSnippet(this);
	}
}
