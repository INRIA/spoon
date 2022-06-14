/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.external.elements.snippets;

import spoon.javadoc.external.parsing.SnippetFileParser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A representation of a {@code @snippet} tag body (or a file referenced by it).
 * <p>
 * Snippet bodies consist of normal code with a list of (potentially overlapping) regions.
 * This class contains lines, regions, and is able to answer "what regions apply here" queries.
 */
public class JavadocSnippetBody {

	private final List<JavadocSnippetMarkupRegion> tags;
	private final List<String> lines;

	public JavadocSnippetBody(List<String> lines, List<JavadocSnippetMarkupRegion> tags) {
		this.tags = tags;
		this.lines = lines;
	}

	/**
	 * @param line the line to check
	 * @return all markup regions that overlap with the given line
	 */
	public List<JavadocSnippetMarkupRegion> getActiveTagsAtLine(int line) {
		return tags.stream()
			.filter(it -> line >= it.getStartLine() && line <= it.getEndLine())
			.collect(Collectors.toList());
	}

	/**
	 * @return all lines of this snippet body
	 */
	public List<String> getLines() {
		return Collections.unmodifiableList(lines);
	}

	/**
	 * @return all markup regions in this snippet
	 */
	public List<JavadocSnippetMarkupRegion> getMarkupRegions() {
		return Collections.unmodifiableList(tags);
	}

	/**
	 * Parses the given text as a snippet body.
	 *
	 * @param text the text to parse
	 * @return the parsed snippet body
	 */
	public static JavadocSnippetBody fromString(String text) {
		List<String> lines = text.lines().collect(Collectors.toList());
		List<JavadocSnippetMarkupRegion> tags = new SnippetFileParser(lines).parse();

		return new JavadocSnippetBody(lines, tags);
	}
}
