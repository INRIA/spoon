package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModuleDirective;
public class CtModuleDirectiveAssert extends AbstractAssert<CtModuleDirectiveAssert, CtModuleDirective> {
	public CtModuleDirectiveAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtModuleDirectiveAssert(CtModuleDirective actual) {
		super(actual, CtModuleDirectiveAssert.class);
	}
}
