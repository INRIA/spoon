/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.api.elements.snippets;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class JavadocSnippetMarkupRegion {

	private final int startLine;
	private final int endLine;
	private final Map<String, String> attributes;
	private final JavadocSnippetRegionType type;

	/**
	 * Creates a new snippet markup region representing some markup tag.
	 *
	 * @param startLine the starting line (inclusive)
	 * @param endLine the end line (inclusive)
	 * @param attributes the attributes of the region
	 * @param type the type of the region
	 */
	public JavadocSnippetMarkupRegion(
		int startLine,
		int endLine,
		Map<String, String> attributes,
		JavadocSnippetRegionType type
	) {
		this.startLine = startLine;
		this.endLine = endLine;
		this.attributes = attributes;
		this.type = type;
	}

	/**
	 * @return the name of this region
	 * @implNote this implementation is equivalent to {@code Optional.ofNullable(attributes.get("region"))}
	 */
	public Optional<String> getName() {
		return Optional.ofNullable(attributes.get("region"));
	}

	/**
	 * @return the line this region starts on (inclusive)
	 */
	public int getStartLine() {
		return startLine;
	}

	/**
	 * @return the line this region ends on (inclusive)
	 */
	public int getEndLine() {
		return endLine;
	}

	/**
	 * @return an unmodifiable map with the region's attributes
	 */
	public Map<String, String> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}

	/**
	 * @return the type of the region
	 */
	public JavadocSnippetRegionType getType() {
		return type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		JavadocSnippetMarkupRegion that = (JavadocSnippetMarkupRegion) o;
		return startLine == that.startLine && endLine == that.endLine && Objects.equals(
			attributes, that.attributes) && type == that.type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(startLine, endLine, attributes, type);
	}

	@Override
	public String toString() {
		return "SnippetMarkupRegion{"
		       + "name='" + getName() + '\''
		       + ", startLine=" + startLine
		       + ", endLine=" + endLine
		       + ", attributes=" + attributes
		       + ", type=" + type
		       + '}';
	}
}
