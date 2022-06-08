package spoon.javadoc.external.parsing;

import spoon.javadoc.external.JavadocTagType;
import spoon.javadoc.external.StandardJavadocTagType;
import spoon.javadoc.external.StringReader;
import spoon.javadoc.external.elements.JavadocBlockTag;
import spoon.javadoc.external.elements.JavadocElement;
import spoon.javadoc.external.elements.JavadocText;
import spoon.javadoc.external.references.JavadocReference;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.List;

public class BlockTagParser {

	private final CtElement documentedElement;
	private final LinkResolver linkResolver;

	public BlockTagParser(CtElement documentedElement, LinkResolver linkResolver) {
		this.documentedElement = documentedElement;
		this.linkResolver = linkResolver;
	}

	public JavadocBlockTag parse(StringReader reader, JavadocTagType type) {
		if (!(type instanceof StandardJavadocTagType)) {
			return new JavadocBlockTag(parseRestFromScratch(reader), type);
		}

		return parseStandardTag(reader, (StandardJavadocTagType) type);
	}

	private JavadocBlockTag parseStandardTag(StringReader reader, StandardJavadocTagType type) {
		switch (type) {
			case AUTHOR:
			case DEPRECATED:
			case HIDDEN:
			case RETURN:
			case SERIAL_DATA:
			case SINCE:
			case VERSION:
				return parseTagOneArgument(reader, type);
			case EXCEPTION:
			case PARAM:
			case PROVIDES:
			case SERIAL:
			case THROWS:
			case USES:
				return parseTagTwoArgument(reader, type);
			case SEE:
				return parseTagSee(reader);
			case SERIAL_FIELD:
				return parseTagSerialField(reader);
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

		if (reader.canRead()) {
			elements.addAll(parseRestFromScratch(reader));
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
			StringBuilder raw = new StringBuilder(reader.readWhile(it -> !Character.isWhitespace(it)));
			if (raw.toString().contains("(")) {
				raw.append(reader.readWhile(it -> it != ')'));
				raw.append(reader.read(1));
			}
			elements.add(
				linkResolver.resolve(raw.toString())
					.<JavadocElement>map(it -> new JavadocReference(raw.toString(), it))
					.orElse(new JavadocText(raw.toString()))
			);
			// label
			elements.add(new JavadocText(reader.readRemaining()));
		}

		return new JavadocBlockTag(elements, StandardJavadocTagType.SEE);
	}

	private JavadocBlockTag parseTagSerialField(StringReader reader) {
		List<JavadocElement> elements = new ArrayList<>();
		elements.add(new JavadocText(reader.readWhile(it -> !Character.isWhitespace(it))));
		elements.add(new JavadocText(reader.readWhile(it -> !Character.isWhitespace(it))));

		elements.addAll(parseRestFromScratch(reader));

		return new JavadocBlockTag(elements, StandardJavadocTagType.SERIAL_FIELD);
	}

	private List<JavadocElement> parseRestFromScratch(StringReader reader) {
		return new JavadocParser(reader, documentedElement).parse();
	}
}
