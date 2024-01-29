package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtJavaDocTag;
public class CtJavaDocTagAssert extends AbstractObjectAssert<CtJavaDocTagAssert, CtJavaDocTag> implements CtJavaDocTagAssertInterface<CtJavaDocTagAssert, CtJavaDocTag> {
	CtJavaDocTagAssert(CtJavaDocTag actual) {
		super(actual, CtJavaDocTagAssert.class);
	}

	@Override
	public CtJavaDocTagAssert self() {
		return this;
	}

	@Override
	public CtJavaDocTag actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
