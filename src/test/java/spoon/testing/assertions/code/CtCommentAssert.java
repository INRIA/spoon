package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtElement;
public class CtCommentAssert extends AbstractAssert<CtCommentAssert, CtComment> {
	public CtCommentAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtCommentAssert(CtComment actual) {
		super(actual, CtCommentAssert.class);
	}
}
