package spoon.javadoc.external.parsing;

import spoon.Launcher;
import spoon.OutputType;
import spoon.javadoc.external.JavadocTagCategory;
import spoon.javadoc.external.JavadocTagType;
import spoon.javadoc.external.StandardJavadocTagType;
import spoon.javadoc.external.StringReader;
import spoon.javadoc.external.elements.JavadocBlockTag;
import spoon.javadoc.external.elements.JavadocElement;
import spoon.javadoc.external.elements.JavadocInlineTag;
import spoon.javadoc.external.elements.JavadocText;
import spoon.javadoc.external.elements.JavadocVisitor;
import spoon.javadoc.external.elements.snippets.JavadocSnippet;
import spoon.javadoc.external.elements.snippets.JavadocSnippetBody;
import spoon.javadoc.external.elements.snippets.JavadocSnippetTag;
import spoon.javadoc.external.references.JavadocReference;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.CtScanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Just a {@code JavadocParser}.
 * {@snippet id = "oh no":
 * 	int a = 20; // @replace substring="=20" replacement=" // Note: Hello" :
 *  int b = 10;
 *}
 */
public class JavadocParser {

	private final StringReader underlying;
	private final CtElement documentedElement;

	public JavadocParser(StringReader underlying, CtElement documentedElement) {
		this.underlying = underlying;
		if (documentedElement instanceof CtType) {
			this.documentedElement = documentedElement;
		} else {
			// Try to generify to the enclosing type
			CtType<?> typeParent = documentedElement.getParent(CtType.class);
			this.documentedElement = typeParent != null ? typeParent : documentedElement;
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
	 * {@snippet lang = java id = "example me" foo = 'bar':
	 * 	   public void HelloWorld(){ //@start region = "foo"
	 * 	      System.out.println("Hello World!"); // @highlight substring="println"
	 *        int a = 10;  // @start foo=bar :
	 *        int a = 10;  // @end
	 * 	   } // @end region=foo
	 *}
	 * <h2><a id="resolution"></a>{@index "Module Resolution"}</h2>
	 *
	 * @param args some argument
	 * @author a poor {@literal man}
	 * 	hello world
	 * @see String#contains(CharSequence) with a label
	 * @see String#replace(char, char)
	 */
	public static void main(String[] args) throws IOException {
		Launcher launcher = new Launcher();
//		launcher.addInputResource("src/main/java/spoon/javadoc/external/parsing/JavadocParser.java");
//		launcher.addInputResource("/tmp/jdk-source");
		launcher.addInputResource("/tmp/jdk-source");
		launcher.getEnvironment().setComplianceLevel(17);
		launcher.getEnvironment().setOutputType(OutputType.NO_OUTPUT);
		CtModel model = launcher.buildModel();

		var scanner = new CtScanner() {
			@Override
			public void scan(CtElement element) {
				if (element == null) {
					return;
				}
				try {
					parseJavadoc(element, PrintAction.NO_PRINT);
					parseSnippetData(element, PrintAction.NO_PRINT);
				} catch (Throwable e) {
					System.out.println("Error caught");
					e.printStackTrace();
					for (CtComment comment : element.getComments()) {
						System.out.println(comment.getRawContent());
					}
					System.out.println("======");
				}

				if (element.getComments().stream().anyMatch(it -> it.getCommentType() == CtComment.CommentType.JAVADOC)) {
//					System.out.println("\n");
				}

				super.scan(element);
			}
		};
		for (CtModule module : model.getAllModules()) {
			System.out.println(module.getSimpleName());
			if (module.getSimpleName().equals("java.security.jgss")) {
				module.accept(scanner);
			}
		}
	}

	enum PrintAction {
		PRINT, NO_PRINT
	}

	private static void parseJavadoc(CtElement elem, PrintAction printAction) {
		JavadocVisitor visitor = new MyJavadocVisitor();
		if (printAction == PrintAction.NO_PRINT) {
			visitor = new JavadocVisitor() {
			};
		}
		for (CtComment comment : elem.getComments()) {
			if (comment.getCommentType() != CtComment.CommentType.JAVADOC) {
				continue;
			}
			for (JavadocElement element : new JavadocParser(comment.getRawContent(), elem).parse()) {
				element.accept(visitor);
			}
		}
	}

	private static void parseSnippetData(CtElement elem, PrintAction printAction) {
		List<JavadocElement> elements = new ArrayList<>();
		for (CtComment comment : elem.getComments()) {
			if (comment.getCommentType() != CtComment.CommentType.JAVADOC) {
				continue;
			}
			elements.addAll(new JavadocParser(comment.getRawContent(), comment).parse());
		}

		for (JavadocElement element : elements) {
			element.accept(new JavadocVisitor<Void>() {
				@Override
				public Void visitSnippet(JavadocSnippet snippet) {
					JavadocText text = (JavadocText) snippet.getElements().get(0);
					if (printAction == PrintAction.NO_PRINT) {
						JavadocSnippetBody.fromString(text.getText()).getTags();
						return null;
					}
					System.out.println("AAYYY");
					List<String> lines = text.getText().lines().collect(Collectors.toList());
					for (int i = 0; i < lines.size(); i++) {
						String line = lines.get(i);
						System.out.printf("%02d  |  %s%n", i, line);
					}
					for (JavadocSnippetTag snippetTag : JavadocSnippetBody.fromString(text.getText()).getTags()) {
						System.out.println(snippetTag);
					}

					return null;
				}
			});
		}
	}

	private static class MyJavadocVisitor implements JavadocVisitor<Void> {
		@Override
		public Void visitInlineTag(JavadocInlineTag tag) {
			System.out.print("{@\033[36m" + tag.getTagType().getName() + "\033[0m");
			for (JavadocElement element : tag.getElements()) {
				System.out.print(" ");
				element.accept(this);
			}
			System.out.print("}");
			return null;
		}

		@Override
		public Void visitBlockTag(JavadocBlockTag tag) {
			System.out.print("@\033[36m" + tag.getTagType().getName() + "\033[0m ");
			for (JavadocElement element : tag.getElements()) {
				element.accept(this);
			}
			System.out.println();
			return null;
		}

		@Override
		public Void visitText(JavadocText text) {
			System.out.print(text.getText());
			return null;
		}

		@Override
		public Void visitReference(JavadocReference reference) {
			System.out.print("\033[31m" + reference.getReference() + "\033[0m");
			return null;
		}

		@Override
		public Void visitSnippet(JavadocSnippet snippet) {
			System.out.print("{@\033[36m" + snippet.getTagType().getName() + "\033[0m ");
			System.out.print(
				snippet.getAttributes()
					.entrySet()
					.stream()
					.sorted(Map.Entry.comparingByKey())
					.map(entry -> entry.getKey() + "='" + entry.getValue() + "'")
					.collect(Collectors.joining(" "))
			);

			System.out.print(" : ");
			for (JavadocElement element : snippet.getElements()) {
				element.accept(this);
			}

			System.out.println("}");
			return null;
		}
	}
}
