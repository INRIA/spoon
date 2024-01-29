package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtModule;
public class CtModuleAssert extends AbstractObjectAssert<CtModuleAssert, CtModule> implements CtModuleAssertInterface<CtModuleAssert, CtModule> {
	CtModuleAssert(CtModule actual) {
		super(actual, CtModuleAssert.class);
	}

	@Override
	public CtModuleAssert self() {
		return this;
	}

	@Override
	public CtModule actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
