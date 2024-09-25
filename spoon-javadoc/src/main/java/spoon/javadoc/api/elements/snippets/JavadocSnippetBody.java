/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.api.elements.snippets;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import spoon.javadoc.api.parsing.SnippetFileParser;

/**
 * A representation of a {@code @snippet} tag body (or a file referenced by it).
 * <p>
 * Snippet bodies consist of normal code with a list of (potentially overlapping) regions. This class contains lines,
 * regions, and is able to answer "what regions apply here" queries.
 */
public class JavadocSnippetBody {

	private final Set<JavadocSnippetMarkupRegion> regions;
	private final List<String> lines;

	private JavadocSnippetBody(List<String> lines, Set<JavadocSnippetMarkupRegion> regions) {
		this.regions = regions;
		this.lines = lines;
	}

	/**
	 * @param line the line to check
	 * @return all markup regions that overlap with the given line
	 */
	public Collection<JavadocSnippetMarkupRegion> getActiveRegionsAtLine(int line) {
		return regions.stream()
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
	public Set<JavadocSnippetMarkupRegion> getMarkupRegions() {
		return Collections.unmodifiableSet(regions);
	}

	/**
	 * Parses the given text as a snippet body.
	 *
	 * @param text the text to parse
	 * @return the parsed snippet body
	 */
	public static JavadocSnippetBody fromString(String text) {
		List<String> lines = text.lines().collect(Collectors.toList());
		Set<JavadocSnippetMarkupRegion> tags = new SnippetFileParser(lines).parse();

		return new JavadocSnippetBody(lines, tags);
	}
}
