package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtFieldWrite;
public class CtFieldWriteAssert extends AbstractObjectAssert<CtFieldWriteAssert, CtFieldWrite<?>> implements CtFieldWriteAssertInterface<CtFieldWriteAssert, CtFieldWrite<?>> {
	CtFieldWriteAssert(CtFieldWrite<?> actual) {
		super(actual, CtFieldWriteAssert.class);
	}

	@Override
	public CtFieldWriteAssert self() {
		return this;
	}

	@Override
	public CtFieldWrite<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
