package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtImport;
public class CtImportAssert extends AbstractAssert<CtImportAssert, CtImport> {
	public CtImportAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtImportAssert(CtImport actual) {
		super(actual, CtImportAssert.class);
	}
}
