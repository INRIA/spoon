package spoon.javadoc.external.parsing;

import spoon.javadoc.external.JavadocTagType;
import spoon.javadoc.external.StandardJavadocTagType;
import spoon.javadoc.external.StringReader;
import spoon.javadoc.external.elements.JavadocElement;
import spoon.javadoc.external.elements.JavadocInlineTag;
import spoon.javadoc.external.elements.JavadocText;

import java.util.ArrayList;
import java.util.List;

public class InlineTagParser {

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
			case SYSTEM_PROPERTY:
			case VALUE:
				return parseStandardTagWithArgument(reader, type);

			case SNIPPET:
				throw new UnsupportedOperationException(":( no snippet yet");
			default:
				throw new AssertionError("Unreachable");
		}
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
