package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtActualTypeContainer;
public class CtActualTypeContainerAssert extends AbstractObjectAssert<CtActualTypeContainerAssert, CtActualTypeContainer> implements CtActualTypeContainerAssertInterface<CtActualTypeContainerAssert, CtActualTypeContainer> {
	CtActualTypeContainerAssert(CtActualTypeContainer actual) {
		super(actual, CtActualTypeContainerAssert.class);
	}

	@Override
	public CtActualTypeContainerAssert self() {
		return this;
	}

	@Override
	public CtActualTypeContainer actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
