package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
public class CtFormalTypeDeclarerAssert extends AbstractObjectAssert<CtFormalTypeDeclarerAssert, CtFormalTypeDeclarer> implements CtFormalTypeDeclarerAssertInterface<CtFormalTypeDeclarerAssert, CtFormalTypeDeclarer> {
	CtFormalTypeDeclarerAssert(CtFormalTypeDeclarer actual) {
		super(actual, CtFormalTypeDeclarerAssert.class);
	}

	@Override
	public CtFormalTypeDeclarerAssert self() {
		return this;
	}

	@Override
	public CtFormalTypeDeclarer actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
