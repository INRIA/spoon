/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.api.parsing;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import spoon.javadoc.api.elements.snippets.JavadocSnippetMarkupRegion;
import spoon.javadoc.api.elements.snippets.JavadocSnippetRegionType;
import spoon.support.Internal;

/**
 * A parser for snippet files and bodies.
 * <p>
 * <em><strong>You want to use
 * {@link spoon.javadoc.api.elements.snippets.JavadocSnippetBody JavadocSnippetBody} instead of directly using this
 * classs.</strong></em>
 */
@Internal
public class SnippetFileParser {

	private final List<String> lines;
	private final Deque<OpenRegion> openRegions;

	/**
	 * @param lines the lines of the snippet body
	 */
	public SnippetFileParser(List<String> lines) {
		this.lines = lines;
		this.openRegions = new ArrayDeque<>();
	}

	/**
	 * Parses the body to a list of markup regions.
	 *
	 * @return the parsed markup regions
	 */
	public Set<JavadocSnippetMarkupRegion> parse() {
		Set<JavadocSnippetMarkupRegion> regions = new HashSet<>();
		boolean closeOnNext = false;

		for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
			if (closeOnNext && !openRegions.isEmpty()) {
				endClosestRegion(lineNumber).ifPresent(regions::add);
				closeOnNext = false;
			}

			StringReader line = new StringReader(lines.get(lineNumber));
			line.readWhile(it -> it != '@');
			if (!line.peek("@")) {
				continue;
			}
			line.read("@");
			String tag = line.readWhile(it -> !Character.isWhitespace(it));

			if (tag.equalsIgnoreCase("end")) {
				endRegion(line, lineNumber).ifPresent(regions::add);
				continue;
			}

			Optional<JavadocSnippetRegionType> regionType = JavadocSnippetRegionType.fromString(
				tag.strip().toLowerCase(Locale.ROOT)
			);
			if (regionType.isEmpty()) {
				continue;
			}

			Map<String, String> attributes = InlineTagParser.parseSnippetAttributes(
				new StringReader(line.readRemaining())
			);
			boolean forNextLine = line.getUnderlying().stripTrailing().endsWith(":");
			boolean shouldClose =
				!attributes.containsKey("region") && regionType.get() != JavadocSnippetRegionType.START;
			closeOnNext = forNextLine && shouldClose;

			int startLine = forNextLine ? lineNumber + 1 : lineNumber;
			openRegions.push(new OpenRegion(startLine, attributes, regionType.get()));

			// A one-line region
			if (shouldClose && !closeOnNext) {
				endRegion(line, lineNumber).ifPresent(regions::add);
			}
		}

		for (int i = 0, end = openRegions.size(); i < end; i++) {
			endClosestRegion(lines.size()).ifPresent(regions::add);
		}

		return regions;
	}

	private Optional<JavadocSnippetMarkupRegion> endRegion(StringReader reader, int line) {
		reader.readWhile(it -> Character.isWhitespace(it) && it != '\n');
		if (openRegions.isEmpty()) {
			return Optional.empty();
		}

		if (reader.peek("\n") || !reader.canRead()) {
			return endClosestRegion(line);
		}

		Map<String, String> attributes = InlineTagParser.parseSnippetAttributes(
			new StringReader(reader.readWhile(it -> it != '\n'))
		);
		String regionName = attributes.get("region");

		return openRegions.stream()
			.filter(it -> it.name.equals(regionName))
			.findFirst()
			.stream()
			.peek(openRegions::remove)
			.findFirst()
			.map(it -> it.close(line));
	}

	private Optional<JavadocSnippetMarkupRegion> endClosestRegion(int endLine) {
		// end without argument
		OpenRegion openRegion = openRegions.pop();
		return Optional.of(openRegion.close(endLine));
	}

	private static class OpenRegion {

		private final int startLine;
		private final String name;
		private final Map<String, String> attributes;
		private final JavadocSnippetRegionType type;

		private OpenRegion(
			int startLine, Map<String, String> attributes, JavadocSnippetRegionType type
		) {
			this.startLine = startLine;
			this.name = attributes.getOrDefault("region", "");
			this.attributes = attributes;
			this.type = type;
		}

		public JavadocSnippetMarkupRegion close(int endLine) {
			return new JavadocSnippetMarkupRegion(startLine, endLine, attributes, type);
		}
	}
}
