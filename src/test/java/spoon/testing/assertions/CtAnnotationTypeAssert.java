package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtAnnotationType;
public class CtAnnotationTypeAssert extends AbstractObjectAssert<CtAnnotationTypeAssert, CtAnnotationType<?>> implements CtAnnotationTypeAssertInterface<CtAnnotationTypeAssert, CtAnnotationType<?>> {
	CtAnnotationTypeAssert(CtAnnotationType<?> actual) {
		super(actual, CtAnnotationTypeAssert.class);
	}

	@Override
	public CtAnnotationTypeAssert self() {
		return this;
	}

	@Override
	public CtAnnotationType<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
