package spoon.javadoc.api.parsing;

import static org.assertj.core.api.Assertions.assertThat;
import static spoon.javadoc.api.StandardJavadocTagType.AUTHOR;
import static spoon.javadoc.api.StandardJavadocTagType.CODE;
import static spoon.javadoc.api.StandardJavadocTagType.DEPRECATED;
import static spoon.javadoc.api.StandardJavadocTagType.DOC_ROOT;
import static spoon.javadoc.api.StandardJavadocTagType.EXCEPTION;
import static spoon.javadoc.api.StandardJavadocTagType.HIDDEN;
import static spoon.javadoc.api.StandardJavadocTagType.INDEX;
import static spoon.javadoc.api.StandardJavadocTagType.INHERIT_DOC;
import static spoon.javadoc.api.StandardJavadocTagType.LINK;
import static spoon.javadoc.api.StandardJavadocTagType.LINKPLAIN;
import static spoon.javadoc.api.StandardJavadocTagType.LITERAL;
import static spoon.javadoc.api.StandardJavadocTagType.PARAM;
import static spoon.javadoc.api.StandardJavadocTagType.PROVIDES;
import static spoon.javadoc.api.StandardJavadocTagType.RETURN;
import static spoon.javadoc.api.StandardJavadocTagType.SEE;
import static spoon.javadoc.api.StandardJavadocTagType.SERIAL;
import static spoon.javadoc.api.StandardJavadocTagType.SERIAL_DATA;
import static spoon.javadoc.api.StandardJavadocTagType.SERIAL_FIELD;
import static spoon.javadoc.api.StandardJavadocTagType.SINCE;
import static spoon.javadoc.api.StandardJavadocTagType.SUMMARY;
import static spoon.javadoc.api.StandardJavadocTagType.SYSTEM_PROPERTY;
import static spoon.javadoc.api.StandardJavadocTagType.THROWS;
import static spoon.javadoc.api.StandardJavadocTagType.USES;
import static spoon.javadoc.api.StandardJavadocTagType.VALUE;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import spoon.javadoc.api.JavadocTagType;
import spoon.javadoc.api.TestHelper;
import spoon.javadoc.api.elements.JavadocBlockTag;
import spoon.javadoc.api.elements.JavadocElement;
import spoon.javadoc.api.elements.JavadocInlineTag;
import spoon.javadoc.api.elements.JavadocReference;
import spoon.javadoc.api.elements.JavadocText;
import spoon.javadoc.api.elements.snippets.JavadocSnippetTag;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

class JavadocParserTest {

	public static final String I_AM_A_VAR = "WOE IS ME";

	@TestFactory
	Stream<DynamicTest> testFactoryForSamples() {
		return TestHelper.parseType(getClass())
			.getMethods()
			.stream()
			.filter(it -> it.getSimpleName().startsWith("sample"))
			.map(JavadocParserTest::buildDynamicTest);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static DynamicTest buildDynamicTest(CtMethod<?> method) {
		return DynamicTest.dynamicTest(
			method.getSimpleName(),
			() -> assertThat(JavadocParser.forElement(method))
				.containsExactlyElementsOf((Iterable) TestHelper.invokeMethod(method))
		);
	}

	// @formatter:off
	/**
	 * This is a small comment. And now a second sentence.
	 * This part can also contain {@literal inline tags}, or <strong>html</strong>.
	 *
	 * @since 1.0
	 * @return The expected AST
	 *
	 * This return tag has a linebreak...
	 */
	// @formatter:on
	private static List<JavadocElement> sampleWithoutReferences() {
		return List.of(
			text(
				"This is a small comment. And now a second sentence.\nThis part can also contain "
			),
			inline(LITERAL, text("inline tags")),
			text(", or <strong>html</strong>.\n\n"),
			block(SINCE, text("1.0")),
			block(RETURN, text("The expected AST\n\nThis return tag has a linebreak..."))
		);
	}

	// @formatter:off
	/**
	 * Some people use {@code to write code}. Sometimes, you also find an {@code {@docRoot}}, normally used to set image
	 * urls: {@docRoot}/foo.html.
	 *
	 * Javadoc can also build an
	 * {@index index Something that serves to guide, point out, or otherwise facilitate reference, especially.} for you.
	 * These indices can also span {@index "multiple words" more than a single word}.
	 *
	 * {@summary This is a cool summary. Not sure people use this tag. It is mostly ignored here, as it is not at the
	 * beginning}.
	 *
	 * Your JVM vendor can be found in {@systemProperty java.vendor}.
	 *
	 * Literals, like {@literal {@literal}} can be wrapped in literal tags.
	 */
	// @formatter:on
	private static List<JavadocElement> sampleManyInlineTags() {
		return List.of(
			text("Some people use "),
			inline(CODE, text("to write code")),
			text(". Sometimes, you also find an "),
			inline(CODE, text("{@docRoot}")),
			text(", normally used to set image\nurls: "),
			inline(DOC_ROOT),
			text("/foo.html.\n\nJavadoc can also build an\n"),

			inline(
				INDEX,
				text("index"),
				text(
					"Something that serves to guide, point out, or otherwise "
					+ "facilitate reference, especially."
				)
			),
			text(" for you.\nThese indices can also span "),
			inline(INDEX, text("multiple words"), text("more than a single word")),
			text(".\n\n"),

			inline(
				SUMMARY,
				text(
					"This is a cool summary. Not sure people use this tag. "
					+ "It is mostly ignored here, as it is not at the\nbeginning")
			),
			text(".\n\nYour JVM vendor can be found in "),

			inline(SYSTEM_PROPERTY, text("java.vendor")),
			text(".\n\nLiterals, like "),
			inline(LITERAL, text("{@literal}")),
			text(" can be wrapped in literal tags.")
		);
	}

	// @formatter:off
	/**
	 * {@inheritDoc} Please inherit the description.
	 *
	 * {@return hello, I am an inline return tag!}
	 */
	// @formatter:on
	private static List<JavadocElement> sampleReturnInline() {
		return List.of(
			inline(INHERIT_DOC),
			text(" Please inherit the description.\n\n"),
			inline(RETURN, text("hello, I am an inline return tag!"))
		);
	}

	// @formatter:off
	/**
	 * @param aParam A parameter!
	 * @return some value
	 * @author I was authored by me
	 * @hidden
	 * @serialData Nothing is serialized
	 * @since The day I wrote this
	 * @deprecated No worries, you can always use me
	 */
	// @formatter:on
	private static List<JavadocElement> sampleBlockTags(int aParam) {
		return List.of(
			block(PARAM, text("aParam"), text("A parameter!")),
			block(RETURN, text("some value")),
			block(AUTHOR, text("I was authored by me")),
			block(HIDDEN),
			block(SERIAL_DATA, text("Nothing is serialized")),
			block(SINCE, text("The day I wrote this")),
			block(DEPRECATED, text("No worries, you can always use me"))
		);
	}

	// @formatter:off
	/**
	 * This method makes no use of {@link String}, {@link String#contains(CharSequence)},
	 * {@link String#CASE_INSENSITIVE_ORDER}, or {@link java.time}.
	 *
	 * To make things nicer, we can {@link String label}, {@link String#substring(int, int) label},
	 * {@link String#CASE_INSENSITIVE_ORDER label} {@link java.lang.invoke them} all.
	 *
	 * Additionally, {@linkplain String this works} {@linkplain String#substring(int) with}
	 * {@linkplain String#CASE_INSENSITIVE_ORDER plain} {@linkplain java.lang links} too.
	 *
	 * People can also embed values: {@value #I_AM_A_VAR}.
	 */
	// @formatter:on
	private static List<JavadocElement> sampleReferencedInlineTags(Factory factory) {
		return List.of(
			text("This method makes no use of "),
			inline(LINK, ref(factory, String.class)),
			text(", "),
			inline(LINK, ref(factory, String.class, "contains", CharSequence.class)),
			text(",\n"),
			inline(LINK, refField(factory, String.class, "CASE_INSENSITIVE_ORDER")),
			text(", or "),
			inline(LINK, refPackage(factory, "java.time")),

			text(".\n\nTo make things nicer, we can "),
			inline(LINK, ref(factory, String.class), text("label")),
			text(", "),
			inline(LINK, ref(factory, String.class, "substring", int.class, int.class), text("label")),
			text(",\n"),
			inline(LINK, refField(factory, String.class, "CASE_INSENSITIVE_ORDER"), text("label")),
			text(" "),
			inline(LINK, refPackage(factory, "java.lang.invoke"), text("them")),
			text(" all.\n\nAdditionally, "),

			inline(LINKPLAIN, ref(factory, String.class), text("this works")),
			text(" "),
			inline(LINKPLAIN, ref(factory, String.class, "substring", int.class), text("with")),
			text("\n"),
			inline(LINKPLAIN, refField(factory, String.class, "CASE_INSENSITIVE_ORDER"), text("plain")),
			text(" "),
			inline(LINKPLAIN, refPackage(factory, "java.lang"), text("links")),
			text(" too.\n\nPeople can also embed values: "),

			inline(VALUE, refField(factory, JavadocParserTest.class, "I_AM_A_VAR")),
			text(".")
		);
	}

	// @formatter:off
	/**
	 * @exception  RuntimeException If something happens
	 * @throws IllegalArgumentException If something else happens
	 * @see Character#codePointAt(char[], int) for more information
	 * @see "somewhere else"
	 * @see <a href="url">label me</a>
	 * @see spoon.javadoc
	 * @see java.base/
	 * @see java.base
	 * @see java.base/java.lang.String
	 */
	// @formatter:on
	private static List<JavadocElement> sampleReferencedBlockTags(Factory factory) {
		return List.of(
			block(EXCEPTION, ref(factory, RuntimeException.class), text("If something happens")),
			block(
				THROWS,
				ref(factory, IllegalArgumentException.class),
				text("If something else happens")
			),
			block(
				SEE,
				ref(factory, Character.class, "codePointAt", char[].class, int.class),
				text("for more information")
			),
			block(SEE, text("somewhere else")),
			block(SEE, text("<a href=\"url\">label me</a>")),
			block(SEE, refPackage(factory, "spoon.javadoc")),
			block(SEE, refModule(factory, "java.base")),
			block(SEE, refModule(factory, "java.base")),
			block(SEE, ref(factory, String.class))
		);
	}

	// @formatter:off
	/**
	 * <pre>
    class Foo {
      int foo = 0;
    }
	 * </pre>
	 */
	// @formatter:on
	private static List<JavadocElement> sampleNoLeadingAsterisks() {
		return List.of(
			text("<pre>\n    class Foo {\n      int foo = 0;\n    }\n</pre>")
		);
	}

	// @formatter:off
	/**
	 * @param <T> a type param
	 */
	// @formatter:on
	private static <T> List<JavadocElement> sampleTypeParam() {
		return List.of(
			block(PARAM, text("<T>"), text("a type param"))
		);
	}

	// @formatter:off
	/**
	 * @provides foo.bar.Baz hello world
	 * @uses foo.bar.Foo hello there
	 */
	// @formatter:on
	private static List<JavadocElement> sampleModuleStuff() {
		return List.of(
			block(PROVIDES, text("foo.bar.Baz"), text("hello world")),
			block(USES, text("foo.bar.Foo"), text("hello there"))
		);
	}

	// @formatter:off
	/**
	 * @provides foo.bar.Baz hello world
	 * @uses foo.bar.Foo hello there
	 * @serialData some data
	 * @serial include
	 * @serial exclude
	 * @serial Some docs
	 * @serialField name type some description
	 */
	// @formatter:on
	private static List<JavadocElement> sampleSerializableStuff() {
		return List.of(
			block(PROVIDES, text("foo.bar.Baz"), text("hello world")),
			block(USES, text("foo.bar.Foo"), text("hello there")),
			block(SERIAL_DATA, text("some data")),
			block(SERIAL, text("include")),
			block(SERIAL, text("exclude")),
			block(SERIAL, text("Some docs")),
			block(SERIAL_FIELD, text("name"), text("type"), text("some description"))
		);
	}

	// @formatter:off
	/**
	 * {@snippet id = "bar" lang = "java":
	 *   class Foo {}
	 *}
	 */
	// @formatter:on
	private static List<JavadocElement> sampleSnippet() {
		return List.of(
			new JavadocSnippetTag(text("\n  class Foo {}\n"), Map.of("id", "bar", "lang", "java"))
		);
	}

	// @formatter:off
	/**
	 * {@link #I_AM_A_VAR}
	 * {@link JavadocParserTest#I_AM_A_VAR}
	 * {@link spoon.javadoc.api.parsing.JavadocParserTest#I_AM_A_VAR}
	 * {@link #text(String)}
	 * {@link #text}
	 * {@link JavadocParserTest#text( String)}
	 * {@link JavadocParserTest#text}
	 * {@link spoon.javadoc.api.parsing.JavadocParserTest#text(String)}
	 * {@link spoon.javadoc.api.parsing.JavadocParserTest#text}
 	 */
	// @formatter:on
	private static List<JavadocElement> sampleLinkFormats(Factory factory) {
		return List.of(
			inline(LINK, refField(factory, JavadocParserTest.class, "I_AM_A_VAR")),
			text("\n"),
			inline(LINK, refField(factory, JavadocParserTest.class, "I_AM_A_VAR")),
			text("\n"),
			inline(LINK, refField(factory, JavadocParserTest.class, "I_AM_A_VAR")),
			text("\n"),
			inline(LINK, ref(factory, JavadocParserTest.class, "text", String.class)),
			text("\n"),
			inline(LINK, ref(factory, JavadocParserTest.class, "text", String.class)),
			text("\n"),
			inline(LINK, ref(factory, JavadocParserTest.class, "text", String.class)),
			text("\n"),
			inline(LINK, ref(factory, JavadocParserTest.class, "text", String.class)),
			text("\n"),
			inline(LINK, ref(factory, JavadocParserTest.class, "text", String.class)),
			text("\n"),
			inline(LINK, ref(factory, JavadocParserTest.class, "text", String.class))
		);
	}

	// @formatter:off
	/**
	 * {@link #text()}
	 * {@link #text(int)}
	 * {@link #text(String}
	 * {@link #text(}
	 */
	// @formatter:on
	private static List<JavadocElement> sampleBrokenLinks(Factory factory) {
		return List.of(
			inline(LINK, ref(factory, JavadocParserTest.class, "text", String.class)),
			text("\n"),
			inline(LINK, ref(factory, JavadocParserTest.class, "text", String.class)),
			text("\n"),
			inline(LINK, ref(factory, JavadocParserTest.class, "text", String.class)),
			text("\n"),
			inline(LINK, ref(factory, JavadocParserTest.class, "text", String.class))
		);
	}

	@Test
	void testClassWithMemberRefs() {
		CtType<?> element = TestHelper.parseType(getClass())
			.getNestedType(ClassWithMemberRef.class.getSimpleName());
		Factory factory = element.getFactory();

		List<JavadocElement> javadoc = JavadocParser.forElement(element);
		assertThat(javadoc).containsExactly(
			inline(LINK, ref(factory, String.class)),
			text(" "),
			inline(LINK, ref(factory, ClassWithMemberRef.class, "String")),
			text(" "),
			inline(LINK, ref(factory, ClassWithMemberRef.class, "foo")),
			text(" "),
			inline(LINK, ref(factory, ClassWithMemberRef.class, "foo")),
			text(" "),
			inline(LINK, new JavadocReference(element.getField("hey").getReference())),
			text(" "),
			inline(LINK, new JavadocReference(element.getField("hey").getReference()))
		);
	}

	/**
	 * {@link String} {@link #String} {@link foo} {@link #foo} {@link hey} {@link #hey}
	 */
	private static class ClassWithMemberRef {
		public void String() {}
		public void foo() {}
		public int hey;
	}

	private static JavadocText text(String text) {
		return new JavadocText(text);
	}

	private static JavadocBlockTag block(JavadocTagType type, JavadocElement... elements) {
		return new JavadocBlockTag(Arrays.asList(elements), type);
	}

	private static JavadocInlineTag inline(JavadocTagType type, JavadocElement... elements) {
		return new JavadocInlineTag(Arrays.asList(elements), type);
	}

	private static JavadocReference ref(Factory factory, Class<?> clazz) {
		return new JavadocReference(factory.createCtTypeReference(clazz));
	}

	private static JavadocReference ref(
		Factory factory, Class<?> clazz, String name, Class<?>... params
	) {
		CtType<?> ctClass = factory.Type().get(clazz);
		for (CtMethod<?> candidate : ctClass.getMethodsByName(name)) {
			if (parametersMatch(candidate, params)) {
				return new JavadocReference(candidate.getReference());
			}
		}

		throw new RuntimeException(
			"Not found: " + clazz + "#" + name + "(" + Arrays.toString(params) + ")"
		);
	}

	private static JavadocReference refField(Factory factory, Class<?> clazz, String name) {
		CtType<?> ctClass = factory.Type().get(clazz);
		return new JavadocReference(ctClass.getField(name).getReference());
	}

	private static JavadocReference refPackage(Factory factory, String name) {
		return new JavadocReference(factory.Package().createReference(name));
	}

	private static JavadocReference refModule(Factory factory, String name) {
		return new JavadocReference(factory.Core().createModuleReference().setSimpleName(name));
	}

	private static boolean parametersMatch(CtMethod<?> method, Class<?>[] expected) {
		List<CtParameter<?>> parameters = method.getParameters();
		if (parameters.size() != expected.length) {
			return false;
		}
		for (int i = 0; i < parameters.size(); i++) {
			CtTypeReference<?> candidateType = parameters.get(i).getType();
			if (candidateType.getActualClass() != expected[i]) {
				return false;
			}
		}
		return true;
	}
}
