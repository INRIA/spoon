package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtAnnotation;
public class CtAnnotationAssert extends AbstractObjectAssert<CtAnnotationAssert, CtAnnotation<?>> implements CtAnnotationAssertInterface<CtAnnotationAssert, CtAnnotation<?>> {
	CtAnnotationAssert(CtAnnotation<?> actual) {
		super(actual, CtAnnotationAssert.class);
	}

	@Override
	public CtAnnotationAssert self() {
		return this;
	}

	@Override
	public CtAnnotation<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
