package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtPackageExport;
public class CtPackageExportAssert extends AbstractObjectAssert<CtPackageExportAssert, CtPackageExport> implements CtPackageExportAssertInterface<CtPackageExportAssert, CtPackageExport> {
	CtPackageExportAssert(CtPackageExport actual) {
		super(actual, CtPackageExportAssert.class);
	}

	@Override
	public CtPackageExportAssert self() {
		return this;
	}

	@Override
	public CtPackageExport actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
