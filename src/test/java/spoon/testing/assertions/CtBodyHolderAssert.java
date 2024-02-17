package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtBodyHolder;
public class CtBodyHolderAssert extends AbstractObjectAssert<CtBodyHolderAssert, CtBodyHolder> implements CtBodyHolderAssertInterface<CtBodyHolderAssert, CtBodyHolder> {
	CtBodyHolderAssert(CtBodyHolder actual) {
		super(actual, CtBodyHolderAssert.class);
	}

	@Override
	public CtBodyHolderAssert self() {
		return this;
	}

	@Override
	public CtBodyHolder actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
