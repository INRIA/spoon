package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtShadowable;
public class CtShadowableAssert extends AbstractObjectAssert<CtShadowableAssert, CtShadowable> implements CtShadowableAssertInterface<CtShadowableAssert, CtShadowable> {
	CtShadowableAssert(CtShadowable actual) {
		super(actual, CtShadowableAssert.class);
	}

	@Override
	public CtShadowableAssert self() {
		return this;
	}

	@Override
	public CtShadowable actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
