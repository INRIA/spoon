package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtAnnotationMethod;
public class CtAnnotationMethodAssert extends AbstractObjectAssert<CtAnnotationMethodAssert, CtAnnotationMethod<?>> implements CtAnnotationMethodAssertInterface<CtAnnotationMethodAssert, CtAnnotationMethod<?>> {
	CtAnnotationMethodAssert(CtAnnotationMethod<?> actual) {
		super(actual, CtAnnotationMethodAssert.class);
	}

	@Override
	public CtAnnotationMethodAssert self() {
		return this;
	}

	@Override
	public CtAnnotationMethod<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
