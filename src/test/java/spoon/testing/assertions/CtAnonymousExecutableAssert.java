package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtAnonymousExecutable;
public class CtAnonymousExecutableAssert extends AbstractObjectAssert<CtAnonymousExecutableAssert, CtAnonymousExecutable> implements CtAnonymousExecutableAssertInterface<CtAnonymousExecutableAssert, CtAnonymousExecutable> {
	CtAnonymousExecutableAssert(CtAnonymousExecutable actual) {
		super(actual, CtAnonymousExecutableAssert.class);
	}

	@Override
	public CtAnonymousExecutableAssert self() {
		return this;
	}

	@Override
	public CtAnonymousExecutable actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
