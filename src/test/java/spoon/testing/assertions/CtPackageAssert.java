package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtPackage;
public class CtPackageAssert extends AbstractObjectAssert<CtPackageAssert, CtPackage> implements CtPackageAssertInterface<CtPackageAssert, CtPackage> {
	CtPackageAssert(CtPackage actual) {
		super(actual, CtPackageAssert.class);
	}

	@Override
	public CtPackageAssert self() {
		return this;
	}

	@Override
	public CtPackage actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
