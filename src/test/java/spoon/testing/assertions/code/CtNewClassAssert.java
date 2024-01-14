package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtElement;
public class CtNewClassAssert extends AbstractAssert<CtNewClassAssert, CtNewClass> {
	public CtNewClassAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtNewClassAssert(CtNewClass actual) {
		super(actual, CtNewClassAssert.class);
	}
}
