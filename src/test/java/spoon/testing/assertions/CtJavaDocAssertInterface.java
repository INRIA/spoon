package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.ListAssert;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
interface CtJavaDocAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtJavaDoc> extends SpoonAssert<A, W> , CtCommentAssertInterface<A, W> {
    default ListAssert<CtJavaDocTag> getTags() {
        return org.assertj.core.api.Assertions.assertThat(actual().getTags());
    }
}