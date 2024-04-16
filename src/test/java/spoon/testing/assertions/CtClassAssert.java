package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtClass;
public class CtClassAssert extends AbstractObjectAssert<CtClassAssert, CtClass<?>> implements CtClassAssertInterface<CtClassAssert, CtClass<?>> {
	CtClassAssert(CtClass<?> actual) {
		super(actual, CtClassAssert.class);
	}

	@Override
	public CtClassAssert self() {
		return this;
	}

	@Override
	public CtClass<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
