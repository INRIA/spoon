package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtRHSReceiver;
public class CtRHSReceiverAssert extends AbstractObjectAssert<CtRHSReceiverAssert, CtRHSReceiver<?>> implements CtRHSReceiverAssertInterface<CtRHSReceiverAssert, CtRHSReceiver<?>> {
	CtRHSReceiverAssert(CtRHSReceiver<?> actual) {
		super(actual, CtRHSReceiverAssert.class);
	}

	@Override
	public CtRHSReceiverAssert self() {
		return this;
	}

	@Override
	public CtRHSReceiver<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
