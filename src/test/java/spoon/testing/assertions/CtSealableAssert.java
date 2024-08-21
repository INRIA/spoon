package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtSealable;
public class CtSealableAssert extends AbstractObjectAssert<CtSealableAssert, CtSealable> implements CtSealableAssertInterface<CtSealableAssert, CtSealable> {
	CtSealableAssert(CtSealable actual) {
		super(actual, CtSealableAssert.class);
	}

	@Override
	public CtSealableAssert self() {
		return this;
	}

	@Override
	public CtSealable actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
