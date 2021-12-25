package spoon.testing.utils;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
/**
 * This class is used to set the line separator to the one used in the test.
 * This is useful when the test is run on windows and the line separator is \r\n.
 * This is needed the line separator is not set correctly in the test and the printer uses the default one.
 * After the testcase the line seperator is set back to the default one.
 * <p>
 * This class is an extension and <b>only</b> can be used in a <b>junit 5</b> test.
 * The usage is as follows:
 * <pre>
 * &#64;ExtendWith(LineSeperatorExtension.class)
 * public void test() { 
 *    // test code
 * }
 * </pre>
 *  @apiNote It would be possible to set the line separator in the test itself but this is code repetition and not very clean.
 * Setting the line spearator one time before all tests would be possible but stops any usage of different line spearators in tests.
 */
public class LineSeperatorExtension implements BeforeEachCallback, AfterEachCallback {

  private String lineSeparator =  System.lineSeparator(); // default separator if somethings is really wrong.
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
