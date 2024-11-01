package spoon.testing.matchers;

import java.util.regex.Pattern;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * A matcher that checks for a regex pattern in a string.
 */
public class RegexFindMatcher extends TypeSafeMatcher<String> {

	private final Pattern regex;

	public RegexFindMatcher(Pattern regex) {
		this.regex = regex;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("contains a match for the regex '")
				.appendText(regex.pattern())
				.appendText("' ");
	}

	@Override
	protected boolean matchesSafely(String item) {
		return regex.matcher(item).find();
	}
}
