package spoon.javadoc.external.parsing;

import spoon.Launcher;
import spoon.javadoc.external.JavadocTagCategory;
import spoon.javadoc.external.JavadocTagType;
import spoon.javadoc.external.StandardJavadocTagType;
import spoon.javadoc.external.StringReader;
import spoon.javadoc.external.elements.JavadocBlockTag;
import spoon.javadoc.external.elements.JavadocElement;
import spoon.javadoc.external.elements.JavadocText;
import spoon.reflect.declaration.CtMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JavadocParser {

	private final StringReader underlying;

	public JavadocParser(String underlying) {
		this.underlying = new StringReader(
			stripStars(underlying.replaceFirst("/\\*\\*", "").replace("*/", "")).strip()
		);
		System.out.println("#######");
		System.out.println(this.underlying.peek(this.underlying.remaining()));
		System.out.println("#######");
	}

	private List<JavadocElement> parse() {
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
			if (inlineTagStarts() || blockTagStarts()) {
				break;
			}
			read.append(underlying.read(1));
		}
		return new JavadocText(read.toString());
	}

	private JavadocElement readInlineTag() {
		StringReader inner = new StringReader(underlying.readBalancedBraced());
		inner.read("{@");
		String tagName = inner.readWhile(it -> it != '}' && !Character.isWhitespace(it));
		inner.read(1); // eat some whitespace

		JavadocTagType tagType = StandardJavadocTagType.fromString(tagName)
			.orElse(JavadocTagType.unknown(tagName, JavadocTagCategory.INLINE));

		return new InlineTagParser().parse(inner, tagType);
	}

	private JavadocElement readBlockTag() {
		underlying.readWhile(Character::isWhitespace);
		underlying.read("@");

		StringBuilder text = new StringBuilder();
		while (underlying.canRead() && !blockTagStarts()) {
			text.append(underlying.read(1));
		}
		StringReader inner = new StringReader(text.toString());

		String name = inner.readWhile(it -> !Character.isWhitespace(it));
		inner.read(1); // eat some whitespace
		String content = inner.read(inner.remaining());

		return new JavadocBlockTag(
			StandardJavadocTagType.fromString(name).orElse(JavadocTagType.unknown(name, JavadocTagCategory.BLOCK)),
			List.of(new JavadocText(content))
		);
	}

	private boolean blockTagStarts() {
		if (!underlying.canRead()) {
			return false;
		}
		StringReader fork = underlying.fork();
		fork.readWhile(Character::isWhitespace);
		return fork.peek() == '@';
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
		Pattern pattern = Pattern.compile("\\s+\\* ?(.*)");
		Matcher matcher = pattern.matcher(line);
		if (!matcher.find()) {
			return line;
		}
		return matcher.group(1);
	}

	/**
	 * Hello world, this is a description.
	 * How are you doing? I am just fine :)
	 * This is an inline link {@link String} and one with a {@link String la
	 * bel}.
	 * <p>
	 * We can also write <em>very HTML</em> {@code code}.
	 * And an index: {@index "Hello world" With a phrase} or {@index without Without a phrase}.
	 *
	 * @param args some argument
	 * @author a poor man
	 * @see String#contains(CharSequence)
	 */
	public static void main(String[] args) {
		Launcher launcher = new Launcher();
		launcher.addInputResource("src/main/java/spoon/javadoc/external/parsing/JavadocParser.java");
		launcher.buildModel();
		CtMethod<?> method = launcher.getFactory().Type().get(JavadocParser.class).getMethodsByName("main").get(0);
		String text = method.getComments().get(0).getRawContent();
		System.out.println(text);
		System.out.println();
		System.out.println();
		for (JavadocElement element : new JavadocParser(text).parse()) {
			System.out.println(element);
		}
	}
}
