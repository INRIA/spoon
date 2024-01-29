package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtAnnotationFieldAccess;
public class CtAnnotationFieldAccessAssert extends AbstractObjectAssert<CtAnnotationFieldAccessAssert, CtAnnotationFieldAccess<?>> implements CtAnnotationFieldAccessAssertInterface<CtAnnotationFieldAccessAssert, CtAnnotationFieldAccess<?>> {
	CtAnnotationFieldAccessAssert(CtAnnotationFieldAccess<?> actual) {
		super(actual, CtAnnotationFieldAccessAssert.class);
	}

	@Override
	public CtAnnotationFieldAccessAssert self() {
		return this;
	}

	@Override
	public CtAnnotationFieldAccess<?> actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
