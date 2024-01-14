package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtPattern;
public class CtPatternAssert extends AbstractAssert<CtPatternAssert, CtPattern> {
	public CtPatternAssert(CtPattern actual) {
		super(actual, CtPatternAssert.class);
	}
}
