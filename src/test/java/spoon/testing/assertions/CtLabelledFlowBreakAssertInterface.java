package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import spoon.reflect.code.CtLabelledFlowBreak;
interface CtLabelledFlowBreakAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtLabelledFlowBreak> extends CtCFlowBreakAssertInterface<A, W> , SpoonAssert<A, W> {
    default AbstractStringAssert<?> getTargetLabel() {
        return org.assertj.core.api.Assertions.assertThat(actual().getTargetLabel());
    }
}