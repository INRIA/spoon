package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtLabelledFlowBreak;
public class CtLabelledFlowBreakAssert extends AbstractObjectAssert<CtLabelledFlowBreakAssert, CtLabelledFlowBreak> implements CtLabelledFlowBreakAssertInterface<CtLabelledFlowBreakAssert, CtLabelledFlowBreak> {
	CtLabelledFlowBreakAssert(CtLabelledFlowBreak actual) {
		super(actual, CtLabelledFlowBreakAssert.class);
	}

	@Override
	public CtLabelledFlowBreakAssert self() {
		return this;
	}

	@Override
	public CtLabelledFlowBreak actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
