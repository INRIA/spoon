package spoon.testing.assertions;
import java.lang.annotation.Annotation;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssert;
import spoon.reflect.code.CtComment;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
public interface CtElementAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtElement> extends SpoonAssert<A, W> {
	default ListAssert<CtAnnotation<? extends Annotation>> getAnnotations() {
		return Assertions.assertThat(actual().getAnnotations());
	}

	default ListAssert<CtComment> getComments() {
		return Assertions.assertThat(actual().getComments());
	}

	default ObjectAssert<SourcePosition> getPosition() {
		return Assertions.assertThatObject(actual().getPosition());
	}

	default AbstractBooleanAssert<?> isImplicit() {
		return Assertions.assertThat(actual().isImplicit());
	}
}
