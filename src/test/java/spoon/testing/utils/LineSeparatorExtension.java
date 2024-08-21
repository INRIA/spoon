package spoon.testing.utils;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * This class is used to set the line separator to a single newline ({@literal \n}).
 * This is useful when the test is run on windows, where the line separator is {@literal \r\n}, as the printer
 * uses the system line separator.
 * After the testcase the line separator is reset.
 * <p>
 * This class is an extension and can <strong>only</strong> be used in a <strong>junit 5</strong> test.
 * The usage is as follows:
 * <pre>
 * &#64;ExtendWith(LineSeparatorExtension.class)
 * public void test() {
 *    // test code
 * }
 * </pre>
 *
 * @apiNote Alternatively, the line separator could be manually set and reset in every individual test. This is not as
 * 	maintainable, makes the tests harder to read and can easily be forgotten.
 */
public class LineSeparatorExtension implements BeforeEachCallback, AfterEachCallback {

	private String lineSeparator = System.lineSeparator(); // default separator if something is really wrong.

	@Override
	public void beforeEach(ExtensionContext context) {
		lineSeparator = System.getProperty("line.separator");
		System.setProperty("line.separator", "\n");
	}

	@Override
	public void afterEach(ExtensionContext context) {
		System.setProperty("line.separator", lineSeparator);
	}
}
