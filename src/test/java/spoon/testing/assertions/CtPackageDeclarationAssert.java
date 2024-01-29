package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtPackageDeclaration;
public class CtPackageDeclarationAssert extends AbstractObjectAssert<CtPackageDeclarationAssert, CtPackageDeclaration> implements CtPackageDeclarationAssertInterface<CtPackageDeclarationAssert, CtPackageDeclaration> {
	CtPackageDeclarationAssert(CtPackageDeclaration actual) {
		super(actual, CtPackageDeclarationAssert.class);
	}

	@Override
	public CtPackageDeclarationAssert self() {
		return this;
	}

	@Override
	public CtPackageDeclaration actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
