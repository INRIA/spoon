package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
public class CtPackageAssert extends AbstractAssert<CtPackageAssert, CtPackage> {
	public CtPackageAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtPackageAssert(CtPackage actual) {
		super(actual, CtPackageAssert.class);
	}
}
