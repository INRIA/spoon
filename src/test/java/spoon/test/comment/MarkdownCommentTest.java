/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2024 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.test.comment;

import org.junit.jupiter.api.extension.ExtendWith;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.testing.utils.LineSeparatorExtension;
import spoon.testing.utils.ModelTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for markdown documentation comments (JEP 467, Java 23+).
 * All examples are drawn from the JEP 467 specification.
 *
 * @see <a href="https://openjdk.org/jeps/467">JEP 467: Markdown Documentation Comments</a>
 */
@ExtendWith(LineSeparatorExtension.class)
public class MarkdownCommentTest {

    // -----------------------------------------------------------------------
    // JEP 467 Example 1 – Simple single-line markdown doc comment
    // -----------------------------------------------------------------------

    @ModelTest(
            code = """
                    class C {
                        /// Returns a hash code value for the object.
                        public int hashCode() { return 0; }
                    }
                    """,
            complianceLevel = 23)
    void testSimpleMarkdownComment(CtModel model) {
        // contract: a single-line /// comment is parsed as CommentType.MARKDOWN
        // and its content has the "/// " prefix stripped
        CtType<?> c = model.getAllTypes().iterator().next();
        CtMethod<?> method = c.getMethodsByName("hashCode").get(0);
        List<CtComment> comments = method.getComments();

        assertThat(comments).hasSize(1);
        CtComment comment = comments.get(0);
        assertThat(comment.getCommentType()).isEqualTo(CtComment.CommentType.MARKDOWN);
        assertThat(comment.getContent()).isEqualTo("Returns a hash code value for the object.");
    }

    // -----------------------------------------------------------------------
    // JEP 467 Example 2 – Multi-paragraph markdown comment (blank /// line)
    // -----------------------------------------------------------------------

    @ModelTest(
            code = """
                    class C {
                        /// First paragraph.
                        ///
                        /// Second paragraph.
                        public void foo() {}
                    }
                    """,
            complianceLevel = 23)
    void testMultiParagraphMarkdownComment(CtModel model) {
        // contract: a multi-line /// comment preserves blank lines in content as empty strings
        // separated by the Spoon line separator (\n)
        CtMethod<?> method = model.getAllTypes().iterator().next()
                .getMethodsByName("foo").get(0);
        List<CtComment> comments = method.getComments();

        assertThat(comments).hasSize(1);
        CtComment comment = comments.get(0);
        assertThat(comment.getCommentType()).isEqualTo(CtComment.CommentType.MARKDOWN);
        assertThat(comment.getContent()).isEqualTo("First paragraph.\n\nSecond paragraph.");
    }

    // -----------------------------------------------------------------------
    // JEP 467 Example 3 – Markdown with a heading (# syntax)
    // -----------------------------------------------------------------------

    @ModelTest(
            code = """
                    class C {
                        /// # A Heading
                        ///
                        /// Some text.
                        public void withHeading() {}
                    }
                    """,
            complianceLevel = 23)
    void testMarkdownWithHeading(CtModel model) {
        // contract: Markdown heading syntax (#) is preserved verbatim in the comment content
        CtMethod<?> method = model.getAllTypes().iterator().next()
                .getMethodsByName("withHeading").get(0);
        CtComment comment = method.getComments().get(0);

        assertThat(comment.getCommentType()).isEqualTo(CtComment.CommentType.MARKDOWN);
        assertThat(comment.getContent()).isEqualTo("# A Heading\n\nSome text.");
    }

    // -----------------------------------------------------------------------
    // JEP 467 Example 4 – Markdown with a fenced code block (``` syntax)
    // -----------------------------------------------------------------------

    @ModelTest(
            code = """
                    class C {
                        /// A summary.
                        ///
                        /// ```java
                        /// int x = 42;
                        /// ```
                        public void withCodeBlock() {}
                    }
                    """,
            complianceLevel = 23)
    void testMarkdownWithCodeBlock(CtModel model) {
        // contract: fenced code blocks (``` ``` ) inside /// comments are preserved in content
        CtMethod<?> method = model.getAllTypes().iterator().next()
                .getMethodsByName("withCodeBlock").get(0);
        CtComment comment = method.getComments().get(0);

        assertThat(comment.getCommentType()).isEqualTo(CtComment.CommentType.MARKDOWN);
        assertThat(comment.getContent()).isEqualTo("A summary.\n\n```java\nint x = 42;\n```");
    }

    // -----------------------------------------------------------------------
    // JEP 467 Example 5 – Markdown with a bullet list (- syntax)
    // -----------------------------------------------------------------------

    @ModelTest(
            code = """
                    class C {
                        /// The method supports:
                        ///
                        /// - case 1
                        /// - case 2
                        public void withList() {}
                    }
                    """,
            complianceLevel = 23)
    void testMarkdownWithList(CtModel model) {
        // contract: Markdown list items are preserved verbatim in the comment content
        CtMethod<?> method = model.getAllTypes().iterator().next()
                .getMethodsByName("withList").get(0);
        CtComment comment = method.getComments().get(0);

        assertThat(comment.getCommentType()).isEqualTo(CtComment.CommentType.MARKDOWN);
        assertThat(comment.getContent()).isEqualTo("The method supports:\n\n- case 1\n- case 2");
    }

    // -----------------------------------------------------------------------
    // JEP 467 Example 6 – Markdown with bold (**) and italic (_) formatting
    // -----------------------------------------------------------------------

    @ModelTest(
            code = """
                    class C {
                        /// **Bold** and _italic_.
                        public void withFormatting() {}
                    }
                    """,
            complianceLevel = 23)
    void testMarkdownWithFormatting(CtModel model) {
        // contract: bold (**) and italic (_) Markdown syntax is preserved verbatim in content
        CtMethod<?> method = model.getAllTypes().iterator().next()
                .getMethodsByName("withFormatting").get(0);
        CtComment comment = method.getComments().get(0);

        assertThat(comment.getCommentType()).isEqualTo(CtComment.CommentType.MARKDOWN);
        assertThat(comment.getContent()).isEqualTo("**Bold** and _italic_.");
    }

    // -----------------------------------------------------------------------
    // JEP 467 Example 7 – Markdown with an inline link ([text](url))
    // -----------------------------------------------------------------------

    @ModelTest(
            code = """
                    class C {
                        /// See [Java documentation](https://docs.oracle.com).
                        public void withLink() {}
                    }
                    """,
            complianceLevel = 23)
    void testMarkdownWithLink(CtModel model) {
        // contract: Markdown link syntax is preserved verbatim in the comment content
        CtMethod<?> method = model.getAllTypes().iterator().next()
                .getMethodsByName("withLink").get(0);
        CtComment comment = method.getComments().get(0);

        assertThat(comment.getCommentType()).isEqualTo(CtComment.CommentType.MARKDOWN);
        assertThat(comment.getContent()).isEqualTo("See [Java documentation](https://docs.oracle.com).");
    }

    // -----------------------------------------------------------------------
    // JEP 467 Example 8 – Markdown comment with @param and @return tags
    // -----------------------------------------------------------------------

    @ModelTest(
            code = """
                    class C {
                        /// Returns the string form.
                        ///
                        /// @param x the value
                        /// @return the string representation
                        public String withParams(int x) { return ""; }
                    }
                    """,
            complianceLevel = 23)
    void testMarkdownWithParamAndReturn(CtModel model) {
        // contract: @param and @return tags in a markdown comment are part of the raw content
        // (markdown comments are CtComment, not CtJavaDoc, so tags remain as plain text)
        CtMethod<?> method = model.getAllTypes().iterator().next()
                .getMethodsByName("withParams").get(0);
        CtComment comment = method.getComments().get(0);

        assertThat(comment.getCommentType()).isEqualTo(CtComment.CommentType.MARKDOWN);
        assertThat(comment.getContent())
                .contains("@param x the value")
                .contains("@return the string representation");
    }

    // -----------------------------------------------------------------------
    // JEP 467 Example 9 – Full-featured markdown doc comment (from the JEP)
    // -----------------------------------------------------------------------

    @ModelTest(
            code = """
                    class C {
                        /// Returns {@code true} if the specified number is
                        /// a [prime number][1], and {@code false} otherwise.
                        ///
                        /// A prime number is defined as a positive integer greater
                        /// than 1, having no divisors other than 1 and itself.
                        ///
                        /// [1]: https://en.wikipedia.org/wiki/Prime_number
                        public boolean isPrime(long n) { return n > 1; }
                    }
                    """,
            complianceLevel = 23)
    void testMarkdownFullJepExample(CtModel model) {
        // contract: the full JEP 467 isPrime example is correctly parsed as a MARKDOWN comment
        // with all content lines preserved and "/// " prefix stripped from each
        CtMethod<?> method = model.getAllTypes().iterator().next()
                .getMethodsByName("isPrime").get(0);
        CtComment comment = method.getComments().get(0);

        assertThat(comment.getCommentType()).isEqualTo(CtComment.CommentType.MARKDOWN);
        String content = comment.getContent();
        assertThat(content).startsWith("Returns {@code true} if the specified number is");
        assertThat(content).contains("[prime number][1]");
        assertThat(content).contains("A prime number is defined as a positive integer greater");
        assertThat(content).contains("[1]: https://en.wikipedia.org/wiki/Prime_number");
    }

    // -----------------------------------------------------------------------
    // Pretty-printing round-trip tests
    // -----------------------------------------------------------------------

    @ModelTest(
            code = """
                    class C {
                        /// Returns a hash code value for the object.
                        public int hashCode() { return 0; }
                    }
                    """,
            complianceLevel = 23)
    void testMarkdownCommentPrettyPrintSingleLine(CtModel model) {
        // contract: the pretty-printer serialises a single-line MARKDOWN comment
        // back using the "/// " prefix (JEP 467 syntax)
        CtMethod<?> method = model.getAllTypes().iterator().next()
                .getMethodsByName("hashCode").get(0);
        CtComment comment = method.getComments().get(0);

        String printed = comment.toString();
        assertThat(printed).contains("/// Returns a hash code value for the object.");
    }

    @ModelTest(
            code = """
                    class C {
                        /// First paragraph.
                        ///
                        /// Second paragraph.
                        public void foo() {}
                    }
                    """,
            complianceLevel = 23)
    void testMarkdownCommentPrettyPrintMultiLine(CtModel model) {
        // contract: the pretty-printer serialises a multi-line MARKDOWN comment with each
        // logical line prefixed by "///" and blank lines represented as bare "///"
        CtMethod<?> method = model.getAllTypes().iterator().next()
                .getMethodsByName("foo").get(0);
        CtComment comment = method.getComments().get(0);

        String printed = comment.toString();
        assertThat(printed).contains("/// First paragraph.");
        assertThat(printed).contains("/// Second paragraph.");
        // blank line between paragraphs is printed as "///" (no trailing space)
        assertThat(printed).contains("///");
    }

    // -----------------------------------------------------------------------
    // CommentType distinctness test
    // -----------------------------------------------------------------------

    @ModelTest(
            code = """
                    class C {
                        /** Javadoc comment. */
                        public void javadocMethod() {}

                        // Inline comment
                        public void inlineMethod() {}

                        /// Markdown doc comment.
                        public void markdownMethod() {}
                    }
                    """,
            complianceLevel = 23)
    void testMarkdownCommentTypeIsDistinct(CtModel model) {
        // contract: MARKDOWN, JAVADOC, and INLINE are distinct comment types;
        // each method gets the correct type assigned during model building
        CtType<?> type = model.getAllTypes().iterator().next();

        CtComment javadoc = type.getMethodsByName("javadocMethod").get(0).getComments().get(0);
        CtComment inline = type.getMethodsByName("inlineMethod").get(0).getComments().get(0);
        CtComment markdown = type.getMethodsByName("markdownMethod").get(0).getComments().get(0);

        assertThat(javadoc.getCommentType()).isEqualTo(CtComment.CommentType.JAVADOC);
        assertThat(inline.getCommentType()).isEqualTo(CtComment.CommentType.INLINE);
        assertThat(markdown.getCommentType()).isEqualTo(CtComment.CommentType.MARKDOWN);
    }

    // -----------------------------------------------------------------------
    // Parameterized test – every example from the resource file
    // -----------------------------------------------------------------------

    @ModelTest(
            value = "./src/test/resources/comment/MarkdownCommentExamples.java",
            complianceLevel = 23)
    void testAllMarkdownCommentsInResourceFile(CtModel model) {
        // contract: every method in MarkdownCommentExamples.java carries exactly one comment
        // and that comment has CommentType.MARKDOWN
        List<CtComment> markdownComments = model.getElements(new TypeFilter<>(CtComment.class))
                .stream()
                .filter(c -> c.getCommentType() == CtComment.CommentType.MARKDOWN)
                .toList();

        // There are 9 documented methods in the resource file
        assertThat(markdownComments).hasSize(9);
        markdownComments.forEach(c ->
                assertThat(c.getCommentType()).isEqualTo(CtComment.CommentType.MARKDOWN));
    }

    // -----------------------------------------------------------------------
    // JEP 467 – code span (backtick) preservation
    // -----------------------------------------------------------------------

    @ModelTest(
            code = """
                    class C {
                        /// Returns the `String` representation of `value`.
                        public String stringify(int value) { return ""; }
                    }
                    """,
            complianceLevel = 23)
    void testMarkdownCodeSpan(CtModel model) {
        // contract: backtick code spans (` `) inside a markdown comment are preserved verbatim
        CtMethod<?> method = model.getAllTypes().iterator().next()
                .getMethodsByName("stringify").get(0);
        CtComment comment = method.getComments().get(0);

        assertThat(comment.getCommentType()).isEqualTo(CtComment.CommentType.MARKDOWN);
        assertThat(comment.getContent())
                .isEqualTo("Returns the `String` representation of `value`.");
    }

    // -----------------------------------------------------------------------
    // JEP 467 – reference-style link preservation
    // -----------------------------------------------------------------------

    @ModelTest(
            code = """
                    class C {
                        /// See the [Java SE documentation][jdk] for details.
                        ///
                        /// [jdk]: https://docs.oracle.com/en/java/
                        public void withRefLink() {}
                    }
                    """,
            complianceLevel = 23)
    void testMarkdownReferenceStyleLink(CtModel model) {
        // contract: Markdown reference-style links are preserved verbatim in the content
        CtMethod<?> method = model.getAllTypes().iterator().next()
                .getMethodsByName("withRefLink").get(0);
        CtComment comment = method.getComments().get(0);

        assertThat(comment.getCommentType()).isEqualTo(CtComment.CommentType.MARKDOWN);
        assertThat(comment.getContent())
                .contains("[Java SE documentation][jdk]")
                .contains("[jdk]: https://docs.oracle.com/en/java/");
    }
}


