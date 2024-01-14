package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtElement;
public class CtReturnAssert extends AbstractAssert<CtReturnAssert, CtReturn> {
	public CtReturnAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtReturnAssert(CtReturn actual) {
		super(actual, CtReturnAssert.class);
	}
}
