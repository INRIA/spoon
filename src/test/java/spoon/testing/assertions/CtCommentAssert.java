package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtComment;
public class CtCommentAssert extends AbstractObjectAssert<CtCommentAssert, CtComment> implements CtCommentAssertInterface<CtCommentAssert, CtComment> {
	CtCommentAssert(CtComment actual) {
		super(actual, CtCommentAssert.class);
	}

	@Override
	public CtCommentAssert self() {
		return this;
	}

	@Override
	public CtComment actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
