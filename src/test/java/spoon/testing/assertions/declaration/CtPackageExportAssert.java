package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackageExport;
public class CtPackageExportAssert extends AbstractAssert<CtPackageExportAssert, CtPackageExport> {
	public CtPackageExportAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtPackageExportAssert(CtPackageExport actual) {
		super(actual, CtPackageExportAssert.class);
	}
}
