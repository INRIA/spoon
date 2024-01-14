package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtConditional;
public class CtConditionalAssert extends AbstractAssert<CtConditionalAssert, CtConditional> {
	public CtConditionalAssert(CtConditional actual) {
		super(actual, CtConditionalAssert.class);
	}
}
