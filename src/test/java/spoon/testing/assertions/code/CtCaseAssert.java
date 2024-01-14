package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtCase;
public class CtCaseAssert extends AbstractAssert<CtCaseAssert, CtCase> {
	public CtCaseAssert(CtCase actual) {
		super(actual, CtCaseAssert.class);
	}
}
