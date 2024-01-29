package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtImport;
public class CtImportAssert extends AbstractObjectAssert<CtImportAssert, CtImport> implements CtImportAssertInterface<CtImportAssert, CtImport> {
	CtImportAssert(CtImport actual) {
		super(actual, CtImportAssert.class);
	}

	@Override
	public CtImportAssert self() {
		return this;
	}

	@Override
	public CtImport actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
