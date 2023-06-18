/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.javadoc.api.parsing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import spoon.javadoc.api.JavadocTagCategory;
import spoon.javadoc.api.JavadocTagType;
import spoon.javadoc.api.StandardJavadocTagType;
import spoon.javadoc.api.elements.JavadocElement;
import spoon.javadoc.api.elements.JavadocText;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;

/**
 * A javadoc parser.
 */
public class JavadocParser {

	private final StringReader underlying;
	private final CtElement documentedElement;

	JavadocParser(StringReader underlying, CtElement documentedElement) {
		this.underlying = underlying;
		if (documentedElement instanceof CtType) {
			this.documentedElement = documentedElement;
		} else {
			// Try to generify to the enclosing type
			CtType<?> typeParent = documentedElement.getParent(CtType.class);
			this.documentedElement = typeParent != null ? typeParent : documentedElement;
		}
	}

	/**
	 * Creates a new javadoc parser from a raw javadoc comment ({@link CtComment#getRawContent()}).
	 *
	 * @param underlying the underlying raw comment
	 * @param documentedElement the element the comment belongs to
	 */
	public JavadocParser(String underlying, CtElement documentedElement) {
		this(
			new StringReader(
				stripStars(underlying.replaceFirst("/\\*\\*", "").replace("*/", "")).strip()
			),
			documentedElement
		);
	}

	/**
	 * @return the parsed representation of the supplied javadoc
	 */
	public List<JavadocElement> parse() {
		if (!underlying.canRead()) {
			return Collections.emptyList();
		}
		List<JavadocElement> elements = new ArrayList<>();
		while (underlying.canRead()) {
			if (inlineTagStarts()) {
				elements.add(readInlineTag());
			} else if (blockTagStarts()) {
				elements.add(readBlockTag());
			} else {
				elements.add(readText());
			}
		}

		return elements;
	}

	private JavadocElement readText() {
		StringBuilder read = new StringBuilder();
		while (underlying.canRead()) {
			read.append(underlying.readWhile(it -> it != '{' && it != '@'));
			if (inlineTagStarts()) {
				break;
			}
			if (blockTagStarts() && endsWithNewline(read.toString())) {
				break;
			}
			read.append(underlying.read(1));
		}
		return new JavadocText(read.toString());
	}

	private boolean endsWithNewline(String input) {
		for (int index = input.length() - 1; index >= 0; index--) {
			if (!Character.isWhitespace(input.charAt(index))) {
				return false;
			}
			if (input.charAt(index) == '\n') {
				return true;
			}
		}
		return false;
	}

	private JavadocElement readInlineTag() {
		StringReader inner = new StringReader(underlying.readBalancedBraced());
		inner.read("@");
		String tagName = inner.readWhile(it -> it != '}' && !Character.isWhitespace(it));
		inner.read(1); // eat some whitespace

		JavadocTagType tagType = StandardJavadocTagType.fromString(tagName)
			.orElse(JavadocTagType.unknown(tagName, JavadocTagCategory.INLINE));

		return new InlineTagParser(
			new LinkResolver(documentedElement, documentedElement.getFactory())
		)
			.parse(inner, tagType);
	}

	private JavadocElement readBlockTag() {
		underlying.readWhile(Character::isWhitespace);
		underlying.read("@");

		StringBuilder text = new StringBuilder();
		while (underlying.canRead()) {
			String read = underlying.read(1);
			if (read.equals("\n") && blockTagStarts()) {
				break;
			}
			text.append(read);
		}
		StringReader inner = new StringReader(text.toString());

		String name = inner.readWhile(it -> !Character.isWhitespace(it));
		inner.read(1); // eat some whitespace

		JavadocTagType tagType = StandardJavadocTagType.fromString(name)
			.orElse(JavadocTagType.unknown(name, JavadocTagCategory.BLOCK));

		return new BlockTagParser(
			documentedElement,
			new LinkResolver(documentedElement, documentedElement.getFactory())
		)
			.parse(inner, tagType);
	}

	private boolean blockTagStarts() {
		StringReader fork = underlying.fork();
		fork.readWhile(Character::isWhitespace);
		return fork.canRead() && fork.peek() == '@';
	}

	private boolean inlineTagStarts() {
		return underlying.matches("{@");
	}

	private static String stripStars(String input) {
		return input.lines()
			.map(JavadocParser::stripStar)
			.collect(Collectors.joining("\n"));
	}

	private static String stripStar(String line) {
		int starIndex = line.indexOf('*');
		if (starIndex < 0 || !line.substring(0, starIndex).isBlank()) {
			return line;
		}

		// Also swallow a single whitespace after the star
		if (starIndex + 1 < line.length() && Character.isWhitespace(line.charAt(starIndex + 1))) {
			return line.substring(starIndex + 2);
		}
		return line.substring(starIndex + 1);
	}

	public static List<JavadocElement> forElement(CtElement element) {
		return element.getComments()
			.stream()
			.filter(comment -> comment instanceof CtJavaDoc)
			.map(comment -> new JavadocParser(comment.getRawContent(), element))
			.map(JavadocParser::parse)
			.flatMap(Collection::stream)
			.collect(Collectors.toCollection(ArrayList::new));
	}

}
