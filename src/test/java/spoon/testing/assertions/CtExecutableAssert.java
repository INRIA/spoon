package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtExecutable;
public class CtExecutableAssert extends AbstractObjectAssert<CtExecutableAssert, CtExecutable<?>> implements CtExecutableAssertInterface<CtExecutableAssert, CtExecutable<?>> {
	CtExecutableAssert(CtExecutable<?> actual) {
		super(actual, CtExecutableAssert.class);
	}

	@Override
	public CtExecutableAssert self() {
		return this;
	}

	@Override
	public CtExecutable<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
