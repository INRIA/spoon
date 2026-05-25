package spoon.test.comment.testclasses;

/**
 * Resource class containing examples from JEP 467 (Markdown Documentation Comments, Java 23+).
 * Each method demonstrates a different feature of markdown documentation comments.
 */
public class MarkdownCommentExamples {

    /// Returns a hash code value for the object.
    public int hashCode() {
        return 0;
    }

    /// First paragraph.
    ///
    /// Second paragraph.
    public void multiParagraph() {
    }

    /// # A Heading
    ///
    /// Some text.
    public void withHeading() {
    }

    /// A summary.
    ///
    /// ```java
    /// int x = 42;
    /// ```
    public void withCodeBlock() {
    }

    /// The method supports:
    ///
    /// - case 1
    /// - case 2
    public void withList() {
    }

    /// **Bold** and _italic_.
    public void withFormatting() {
    }

    /// See [Java documentation](https://docs.oracle.com).
    public void withLink() {
    }

    /// Returns the string form.
    ///
    /// @param x the value
    /// @return the string representation
    public String withParams(int x) {
        return "";
    }

    /// Returns {@code true} if the specified number is
    /// a [prime number][1], and {@code false} otherwise.
    ///
    /// A prime number is defined as a positive integer greater
    /// than 1, having no divisors other than 1 and itself.
    ///
    /// [1]: https://en.wikipedia.org/wiki/Prime_number
    public boolean isPrime(long n) {
        return n > 1;
    }
}

