package spoon.javadoc.external.parsing;

import spoon.Launcher;
import spoon.javadoc.external.JavadocTagCategory;
import spoon.javadoc.external.JavadocTagType;
import spoon.javadoc.external.StandardJavadocTagType;
import spoon.javadoc.external.StringReader;
import spoon.javadoc.external.elements.JavadocBlockTag;
import spoon.javadoc.external.elements.JavadocElement;
import spoon.javadoc.external.elements.JavadocInlineTag;
import spoon.javadoc.external.elements.JavadocText;
import spoon.javadoc.external.elements.JavadocVisitor;
import spoon.javadoc.external.references.JavadocReference;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JavadocParser {

	private final StringReader underlying;
	private final CtElement documentedElement;

	public JavadocParser(StringReader underlying, CtElement documentedElement) {
		this.underlying = underlying;
		if (documentedElement instanceof CtType) {
			this.documentedElement = documentedElement;
		} else {
			this.documentedElement = documentedElement.getParent(CtType.class);
		}
	}

	public JavadocParser(String underlying, CtElement documentedElement) {
		this(
			new StringReader(
				stripStars(underlying.replaceFirst("/\\*\\*", "").replace("*/", "")).strip()
			),
			documentedElement
		);
	}

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
	 * This is an inline link {@link String} and one with a {@link String label}
	 * and a {@link String#CASE_INSENSITIVE_ORDER field} and {@link String#replace(char, char) with a space}.
	 * {@link java.lang.annotation.Target @Target} chained to @Target.
	 * <p>
	 * We can also write <em>very HTML</em> {@code code}.
	 * And an index: {@index "Hello world" With a phrase} or {@index without Without a phrase}.
	 *
	 * @param args some argument
	 * @author a poor {@literal man}
	 * 	hello world
	 * @see String#contains(CharSequence) with a label
	 * @see String#replace(char, char)
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

		List<JavadocElement> elements = new JavadocParser(text, method).parse();
		JavadocVisitor visitor = new MyJavadocVisitor();
		for (JavadocElement element : elements) {
			element.accept(visitor);
		}
	}

	private static class MyJavadocVisitor implements JavadocVisitor {
		@Override
		public void visitInlineTag(JavadocInlineTag tag) {
			System.out.print("{@\033[36m" + tag.getTagType().getName() + "\033[0m");
			for (JavadocElement element : tag.getElements()) {
				System.out.print(" ");
				element.accept(this);
			}
			System.out.print("}");
		}

		@Override
		public void visitBlockTag(JavadocBlockTag tag) {
			System.out.print("@\033[36m" + tag.getTagType().getName() + "\033[0m ");
			for (JavadocElement element : tag.getElements()) {
				element.accept(this);
			}
			System.out.println();
		}

		@Override
		public void visitText(JavadocText text) {
			System.out.print(text.getText());
		}

		@Override
		public void visitReference(JavadocReference reference) {
			System.out.print("\033[31m" + reference.getReference() + "\033[0m");
		}
	}
}
