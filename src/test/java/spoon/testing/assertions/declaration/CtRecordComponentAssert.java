package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtRecordComponent;
public class CtRecordComponentAssert extends AbstractAssert<CtRecordComponentAssert, CtRecordComponent> {
	public CtRecordComponentAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtRecordComponentAssert(CtRecordComponent actual) {
		super(actual, CtRecordComponentAssert.class);
	}
}
