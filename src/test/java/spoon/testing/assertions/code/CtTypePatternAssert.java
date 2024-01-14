package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtTypePattern;
public class CtTypePatternAssert extends AbstractAssert<CtTypePatternAssert, CtTypePattern> {
	public CtTypePatternAssert(CtTypePattern actual) {
		super(actual, CtTypePatternAssert.class);
	}
}
