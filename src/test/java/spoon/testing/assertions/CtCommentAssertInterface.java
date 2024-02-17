package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;
import spoon.reflect.code.CtComment;
public interface CtCommentAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtComment> extends SpoonAssert<A, W> , CtStatementAssertInterface<A, W> {
	default ObjectAssert<CtComment.CommentType> getCommentType() {
		return Assertions.assertThatObject(actual().getCommentType());
	}

	default AbstractStringAssert<?> getContent() {
		return Assertions.assertThat(actual().getContent());
	}
}
