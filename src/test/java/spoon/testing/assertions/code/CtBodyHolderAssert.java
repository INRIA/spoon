package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.declaration.CtElement;
public class CtBodyHolderAssert extends AbstractAssert<CtBodyHolderAssert, CtBodyHolder> {
	public CtBodyHolderAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtBodyHolderAssert(CtBodyHolder actual) {
		super(actual, CtBodyHolderAssert.class);
	}
}
