package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtModuleDirective;
public class CtModuleDirectiveAssert extends AbstractObjectAssert<CtModuleDirectiveAssert, CtModuleDirective> implements CtModuleDirectiveAssertInterface<CtModuleDirectiveAssert, CtModuleDirective> {
	CtModuleDirectiveAssert(CtModuleDirective actual) {
		super(actual, CtModuleDirectiveAssert.class);
	}

	@Override
	public CtModuleDirectiveAssert self() {
		return this;
	}

	@Override
	public CtModuleDirective actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
