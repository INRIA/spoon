package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtCFlowBreak;
public class CtCFlowBreakAssert extends AbstractObjectAssert<CtCFlowBreakAssert, CtCFlowBreak> implements CtCFlowBreakAssertInterface<CtCFlowBreakAssert, CtCFlowBreak> {
	CtCFlowBreakAssert(CtCFlowBreak actual) {
		super(actual, CtCFlowBreakAssert.class);
	}

	@Override
	public CtCFlowBreakAssert self() {
		return this;
	}

	@Override
	public CtCFlowBreak actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
