package spoon.javadoc.external.parsing;

import spoon.javadoc.external.JavadocTagType;
import spoon.javadoc.external.StandardJavadocTagType;
import spoon.javadoc.external.StringReader;
import spoon.javadoc.external.elements.JavadocElement;
import spoon.javadoc.external.elements.JavadocInlineTag;
import spoon.javadoc.external.elements.JavadocText;
import spoon.javadoc.external.references.JavadocReference;

import java.util.ArrayList;
import java.util.List;

public class InlineTagParser {

	private final LinkResolver linkResolver;

	public InlineTagParser(LinkResolver linkResolver) {
		this.linkResolver = linkResolver;
	}

	public JavadocInlineTag parse(StringReader reader, JavadocTagType type) {
		if (!(type instanceof StandardJavadocTagType)) {
			String content = reader.readRemaining();
			return new JavadocInlineTag(List.of(new JavadocText(content)), type);
		}

		return parseStandardTag(reader, (StandardJavadocTagType) type);
	}

	private JavadocInlineTag parseStandardTag(StringReader reader, StandardJavadocTagType type) {
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
				return parseLinkTag(reader, type);
			case SYSTEM_PROPERTY:
			case VALUE:
				return parseStandardTagWithArgument(reader, type);

			case SNIPPET:
				throw new UnsupportedOperationException(":( no snippet yet");
			default:
				throw new AssertionError("Unreachable");
		}
	}

	private JavadocInlineTag parseLinkTag(StringReader reader, StandardJavadocTagType type) {
		StringBuilder referenceText = new StringBuilder(reader.readWhile(it -> !Character.isWhitespace(it)));
		if (referenceText.toString().contains("(")) {
			referenceText.append(reader.readWhile(it -> it != ')'));
			referenceText.append(reader.read(1));

			// Skip whitespace between reference and label
			if (Character.isWhitespace(reader.peek())) {
				reader.read(1);
			}
		}
		List<JavadocElement> elements = new ArrayList<>();

		elements.add(
			linkResolver.resolve(referenceText.toString())
				.<JavadocElement>map(ctReference -> new JavadocReference(referenceText.toString(), ctReference))
				.orElse(new JavadocText(referenceText.toString()))
		);

		String label = reader.readRemaining().strip();
		if (!label.isEmpty()) {
			elements.add(new JavadocText(label));
		}

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
		reader.read("\" ");

		// And our description
		if (reader.canRead()) {
			elements.add(new JavadocText(reader.readRemaining()));
		}

		return new JavadocInlineTag(elements, StandardJavadocTagType.INDEX);
	}

	private JavadocInlineTag parseStandardTagNoArgument(StringReader reader, StandardJavadocTagType type) {
		JavadocText content = new JavadocText(reader.readRemaining());

		return new JavadocInlineTag(List.of(content), type);
	}

	private JavadocInlineTag parseStandardTagWithArgument(StringReader reader, StandardJavadocTagType type) {
		JavadocText firstArgument = new JavadocText(reader.readWhile(it -> !Character.isWhitespace(it)));

		// Swallow one space after it
		if (reader.canRead()) {
			reader.read(1);
		}

		List<JavadocElement> elements = new ArrayList<>();
		elements.add(firstArgument);

		// Read the rest if there is any
		if (reader.canRead()) {
			elements.add(new JavadocText(reader.readRemaining()));
		}

		return new JavadocInlineTag(elements, type);
	}
}
