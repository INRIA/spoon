package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.declaration.CtElement;
public class CtSwitchAssert extends AbstractAssert<CtSwitchAssert, CtSwitch> {
	public CtSwitchAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtSwitchAssert(CtSwitch actual) {
		super(actual, CtSwitchAssert.class);
	}
}
