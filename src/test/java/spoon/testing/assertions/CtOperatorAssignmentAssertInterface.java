package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.ObjectAssert;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtOperatorAssignment;
interface CtOperatorAssignmentAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtOperatorAssignment<?, ?>> extends SpoonAssert<A, W> , CtAssignmentAssertInterface<A, W> {
    default ObjectAssert<BinaryOperatorKind> getKind() {
        return org.assertj.core.api.Assertions.assertThatObject(actual().getKind());
    }
}