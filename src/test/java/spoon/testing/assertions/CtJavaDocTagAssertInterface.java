package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;
import spoon.reflect.code.CtJavaDocTag;
public interface CtJavaDocTagAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtJavaDocTag> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {
	default AbstractStringAssert<?> getContent() {
		return Assertions.assertThat(actual().getContent());
	}

	default AbstractStringAssert<?> getParam() {
		return Assertions.assertThat(actual().getParam());
	}

	default AbstractStringAssert<?> getRealName() {
		return Assertions.assertThat(actual().getRealName());
	}

	default ObjectAssert<CtJavaDocTag.TagType> getType() {
		return Assertions.assertThatObject(actual().getType());
	}
}
