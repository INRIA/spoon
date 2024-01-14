package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
public class CtConstructorAssert extends AbstractAssert<CtConstructorAssert, CtConstructor> {
	public CtConstructorAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtConstructorAssert(CtConstructor actual) {
		super(actual, CtConstructorAssert.class);
	}
}
