package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
import spoon.reflect.code.CtLabelledFlowBreak;
public interface CtLabelledFlowBreakAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtLabelledFlowBreak> extends CtCFlowBreakAssertInterface<A, W> , SpoonAssert<A, W> {
	default AbstractStringAssert<?> getTargetLabel() {
		return Assertions.assertThat(actual().getTargetLabel());
	}
}
