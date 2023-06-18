/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.api.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spoon.javadoc.api.JavadocTagType;
import spoon.javadoc.api.StandardJavadocTagType;
import spoon.javadoc.api.elements.JavadocElement;
import spoon.javadoc.api.elements.JavadocInlineTag;
import spoon.javadoc.api.elements.JavadocReference;
import spoon.javadoc.api.elements.JavadocText;
import spoon.javadoc.api.elements.snippets.JavadocSnippetTag;

/**
 * A parser for inline tags
 */
class InlineTagParser {

	private final LinkResolver linkResolver;

	/**
	 * @param linkResolver the link resolver to use
	 */
	InlineTagParser(LinkResolver linkResolver) {
		this.linkResolver = linkResolver;
	}

	/**
	 * Parses a given reader as a single inline tag of the given type.
	 *
	 * @param reader the reader to parse
	 * @param type the type of the tag
	 * @return the created inline tag
	 */
	public JavadocInlineTag parse(StringReader reader, JavadocTagType type) {
		if (!(type instanceof StandardJavadocTagType)) {
			String content = reader.readRemaining();
			return new JavadocInlineTag(makeTextIfNotEmpty(content), type);
		}

		return parseStandardTag(reader, (StandardJavadocTagType) type);
	}

	private JavadocInlineTag parseStandardTag(StringReader reader, StandardJavadocTagType type) {
		reader.readWhile(Character::isWhitespace);
		switch (type) {
			case CODE:
			case DOC_ROOT:
			case INHERIT_DOC:
			case LITERAL:
			case RETURN:
			case SUMMARY:
				return parseStandardTagNoArgument(reader, type);

			case INDEX:
				return parseIndexTag(reader);

			case LINK:
			case LINKPLAIN:
			case VALUE:
				return parseLinkTag(reader, type);
			case SEE:
				return parseLinkTag(reader, StandardJavadocTagType.LINK);
			case SYSTEM_PROPERTY:
				return parseStandardTagWithArgument(reader, type);

			case SNIPPET:
				return parseSnippetTag(reader);
			default:
				throw new AssertionError("Unreachable");
		}
	}

	private JavadocInlineTag parseLinkTag(StringReader reader, StandardJavadocTagType type) {
		String referenceText = reader.readWhile(it -> !Character.isWhitespace(it));
		if (referenceText.contains("(") && !referenceText.contains(")")) {
			referenceText += reader.readWhile(it -> it != ')');
			referenceText += reader.read(1);

			// Skip whitespace between reference and label
			if (reader.canRead() && Character.isWhitespace(reader.peek())) {
				reader.read(1);
			}
		}
		List<JavadocElement> elements = new ArrayList<>();

		elements.add(
			linkResolver.resolve(referenceText)
				.<JavadocElement>map(JavadocReference::new)
				.orElse(new JavadocText(referenceText))
		);

		String label = reader.readRemaining().strip();
		elements.addAll(makeTextIfNotEmpty(label));

		return new JavadocInlineTag(elements, type);
	}

	private JavadocInlineTag parseIndexTag(StringReader reader) {
		if (!reader.matches("\"")) {
			return parseStandardTagWithArgument(reader, StandardJavadocTagType.INDEX);
		}

		List<JavadocElement> elements = new ArrayList<>();

		// @index "phrase" description
		reader.read("\"");
		elements.add(new JavadocText(reader.readWhile(it -> it != '"')));

		// Closing paren, I guess that might be missing...
		reader.read("\"");
		if (reader.canRead() && Character.isWhitespace(reader.peek())) {
			reader.read(1);
		}

		// And our description
		if (reader.canRead()) {
			elements.add(new JavadocText(reader.readRemaining()));
		}

		return new JavadocInlineTag(elements, StandardJavadocTagType.INDEX);
	}

	private JavadocInlineTag parseStandardTagNoArgument(StringReader reader, StandardJavadocTagType type) {
		String content = reader.readRemaining();

		return new JavadocInlineTag(makeTextIfNotEmpty(content), type);
	}

	private JavadocInlineTag parseStandardTagWithArgument(StringReader reader, StandardJavadocTagType type) {
		String firstArgument = reader.readWhile(it -> !Character.isWhitespace(it));

		// Swallow one space after it
		if (reader.canRead()) {
			reader.read(1);
		}

		List<JavadocElement> elements = new ArrayList<>(makeTextIfNotEmpty(firstArgument));

		// Read the rest if there is any
		if (reader.canRead()) {
			elements.add(new JavadocText(reader.readRemaining()));
		}

		return new JavadocInlineTag(elements, type);
	}

	private JavadocInlineTag parseSnippetTag(StringReader reader) {
		Map<String, String> attributes = parseSnippetAttributes(new StringReader(reader.readWhile(it -> it != ':')));

		reader.readWhile(Character::isWhitespace);
		if (reader.canRead() && reader.peek() == ':') {
			reader.read(":");
		}

		JavadocText content = new JavadocText(reader.readRemaining());

		return new JavadocSnippetTag(content, attributes);
	}

	static Map<String, String> parseSnippetAttributes(StringReader reader) {
		Map<String, String> attributes = new HashMap<>();
		reader.readWhile(Character::isWhitespace);

		while (reader.canRead()) {
			String name = reader.readWhile(it -> it != '=').strip();
			if (!reader.canRead() || reader.peek() != '=') {
				break;
			}
			reader.read("=");
			reader.readWhile(Character::isWhitespace);

			String value = reader.readPotentiallyQuoted();
			attributes.put(name, value);

			reader.readWhile(Character::isWhitespace);
		}

		return attributes;
	}

	private static List<JavadocElement> makeTextIfNotEmpty(String text) {
		return text.isEmpty() ? List.of() : List.of(new JavadocText(text));
	}
}
