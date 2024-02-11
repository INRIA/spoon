package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtJavaDoc;
public class CtJavaDocAssert extends AbstractObjectAssert<CtJavaDocAssert, CtJavaDoc> implements CtJavaDocAssertInterface<CtJavaDocAssert, CtJavaDoc> {
	CtJavaDocAssert(CtJavaDoc actual) {
		super(actual, CtJavaDocAssert.class);
	}

	@Override
	public CtJavaDocAssert self() {
		return this;
	}

	@Override
	public CtJavaDoc actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
