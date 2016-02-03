package spoon.testing;

import org.junit.Test;

import static spoon.testing.Assert.assertThat;

public class FileAssertTest {
	public static final String PATH = "./src/test/java/spoon/testing/testclasses/";

	@Test
	public void testEqualsBetweenTwoSameFile() throws Exception {
		final String actual = PATH + "Foo.java";
		assertThat(actual).isEqualTo(actual);
	}

	@Test(expected = AssertionError.class)
	public void testEqualsBetweenTwoDifferentFile() throws Exception {
		assertThat(PATH + "Foo.java").isEqualTo(PATH + "Bar.java");
	}
}
