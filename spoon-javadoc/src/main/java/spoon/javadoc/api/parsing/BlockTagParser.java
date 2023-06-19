/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.api.parsing;

import java.util.ArrayList;
import java.util.List;
import spoon.javadoc.api.JavadocTagType;
import spoon.javadoc.api.StandardJavadocTagType;
import spoon.javadoc.api.elements.JavadocBlockTag;
import spoon.javadoc.api.elements.JavadocElement;
import spoon.javadoc.api.elements.JavadocInlineTag;
import spoon.javadoc.api.elements.JavadocReference;
import spoon.javadoc.api.elements.JavadocText;
import spoon.reflect.declaration.CtElement;

/**
 * A parser for javadoc block tags.
 */
class BlockTagParser {

	private final CtElement documentedElement;
	private final LinkResolver linkResolver;

	/**
	 * Creates a new parser with a given element as context and a given link resolver.
	 *
	 * @param documentedElement the element the comment belongs to. Used for relative link lookups
	 * @param linkResolver the link resolver for {@code @link} and {@code @see} tags
	 */
	BlockTagParser(CtElement documentedElement, LinkResolver linkResolver) {
		this.documentedElement = documentedElement;
		this.linkResolver = linkResolver;
	}

	/**
	 * Parses a blocktag found in the passed reader. Assumes the complete reader contains a single tag.
	 *
	 * @param reader the reader to read from
	 * @param type the type of the block tag
	 * @return the created block or inline tag
	 */
	public JavadocElement parse(StringReader reader, JavadocTagType type) {
		if (!(type instanceof StandardJavadocTagType)) {
			return new JavadocBlockTag(parseRestFromScratch(reader), type);
		}

		return parseStandardTag(reader, (StandardJavadocTagType) type);
	}

	private JavadocElement parseStandardTag(StringReader reader, StandardJavadocTagType type) {
		// Skip preceding whitespace
		reader.readWhile(Character::isWhitespace);

		switch (type) {
			case AUTHOR:
			case DEPRECATED:
			case HIDDEN:
			case RETURN:
			case SERIAL_DATA:
			case SINCE:
			case SERIAL:
			case VERSION:
				return parseTagOneArgument(reader, type);
			case THROWS:
			case EXCEPTION:
				return parseException(reader, type);
			case PARAM:
			case PROVIDES:
			case USES:
				return parseTagTwoArgument(reader, type);
			case SEE:
				return parseTagSee(reader);
			case SERIAL_FIELD:
				return parseTagSerialField(reader);
			case INHERIT_DOC:
				return new JavadocInlineTag(List.of(), StandardJavadocTagType.INHERIT_DOC);
			default:
				throw new AssertionError("Unreachable, was " + type);
		}
	}

	private JavadocBlockTag parseTagOneArgument(StringReader reader, StandardJavadocTagType type) {
		return new JavadocBlockTag(parseRestFromScratch(reader), type);
	}

	private JavadocBlockTag parseTagTwoArgument(StringReader reader, StandardJavadocTagType type) {
		List<JavadocElement> elements = new ArrayList<>();

		elements.add(new JavadocText(reader.readWhile(it -> !Character.isWhitespace(it))));

		swallowOneChar(reader);

		if (reader.canRead()) {
			elements.addAll(parseRestFromScratch(reader));
		}

		return new JavadocBlockTag(elements, type);
	}

	private JavadocBlockTag parseException(StringReader reader, StandardJavadocTagType type) {
		List<JavadocElement> elements = new ArrayList<>();

		String referenceString = reader.readWhile(it -> !Character.isWhitespace(it));
		elements.add(linkResolver.resolve(referenceString)
			.<JavadocElement>map(JavadocReference::new)
			.orElse(new JavadocText(referenceString)));

		swallowOneChar(reader);

		if (reader.canRead()) {
			elements.add(new JavadocText(reader.readRemaining()));
		}

		return new JavadocBlockTag(elements, type);
	}

	private JavadocBlockTag parseTagSee(StringReader reader) {
		List<JavadocElement> elements = new ArrayList<>();

		if (reader.matches("\"")) {
			// as string
			reader.read("\"");
			elements.add(new JavadocText(reader.readWhile(it -> it != '"')));
			reader.read("\"");
		} else if (reader.matches("<")) {
			// html tag
			elements.add(new JavadocText(reader.readRemaining()));
		} else {
			String reference = reader.readWhile(it -> !Character.isWhitespace(it));
			// read "@see #foo(int, char)" completely
			if (reference.contains("(") && !reference.endsWith(")")) {
				reference += reader.readWhile(it -> it != ')');
				reader.read(1);
			}
			elements.add(
				linkResolver.resolve(reference)
					.<JavadocElement>map(JavadocReference::new)
					.orElse(new JavadocText(reference))
			);

			// label
			swallowOneChar(reader);
			if (reader.canRead()) {
				elements.add(new JavadocText(reader.readRemaining()));
			}
		}

		return new JavadocBlockTag(elements, StandardJavadocTagType.SEE);
	}

	private static void swallowOneChar(StringReader reader) {
		if (reader.canRead()) {
			reader.read(1);
		}
	}

	private JavadocBlockTag parseTagSerialField(StringReader reader) {
		List<JavadocElement> elements = new ArrayList<>();
		elements.add(new JavadocText(reader.readWhile(it -> !Character.isWhitespace(it))));
		swallowOneChar(reader);
		elements.add(new JavadocText(reader.readWhile(it -> !Character.isWhitespace(it))));
		swallowOneChar(reader);

		elements.addAll(parseRestFromScratch(reader));

		return new JavadocBlockTag(elements, StandardJavadocTagType.SERIAL_FIELD);
	}

	private List<JavadocElement> parseRestFromScratch(StringReader reader) {
		return new JavadocParser(reader, documentedElement).parse();
	}
}
