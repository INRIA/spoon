package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtComment;
public class CtCommentAssert extends AbstractAssert<CtCommentAssert, CtComment> {
	public CtCommentAssert(CtComment actual) {
		super(actual, CtCommentAssert.class);
	}
}
