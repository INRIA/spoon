package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.declaration.CtElement;
public class CtFieldWriteAssert extends AbstractAssert<CtFieldWriteAssert, CtFieldWrite> {
	public CtFieldWriteAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtFieldWriteAssert(CtFieldWrite actual) {
		super(actual, CtFieldWriteAssert.class);
	}
}
