package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
public class CtExecutableAssert extends AbstractAssert<CtExecutableAssert, CtExecutable> {
	public CtExecutableAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtExecutableAssert(CtExecutable actual) {
		super(actual, CtExecutableAssert.class);
	}
}
