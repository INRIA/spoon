package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.ObjectAssert;
import spoon.reflect.code.CtJavaDocTag;
interface CtJavaDocTagAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtJavaDocTag> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {
    default AbstractStringAssert<?> getContent() {
        return org.assertj.core.api.Assertions.assertThat(actual().getContent());
    }

    default AbstractStringAssert<?> getParam() {
        return org.assertj.core.api.Assertions.assertThat(actual().getParam());
    }

    default AbstractStringAssert<?> getRealName() {
        return org.assertj.core.api.Assertions.assertThat(actual().getRealName());
    }

    default ObjectAssert<CtJavaDocTag.TagType> getType() {
        return org.assertj.core.api.Assertions.assertThatObject(actual().getType());
    }
}