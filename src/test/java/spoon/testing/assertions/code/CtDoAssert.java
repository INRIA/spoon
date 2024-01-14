package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtDo;
import spoon.reflect.declaration.CtElement;
public class CtDoAssert extends AbstractAssert<CtDoAssert, CtDo> {
	public CtDoAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtDoAssert(CtDo actual) {
		super(actual, CtDoAssert.class);
	}
}
