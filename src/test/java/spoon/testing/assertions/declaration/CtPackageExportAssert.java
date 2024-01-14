package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtPackageExport;
public class CtPackageExportAssert extends AbstractAssert<CtPackageExportAssert, CtPackageExport> {
	public CtPackageExportAssert(CtPackageExport actual) {
		super(actual, CtPackageExportAssert.class);
	}
}
