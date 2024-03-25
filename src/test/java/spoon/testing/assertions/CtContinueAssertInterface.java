package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtContinue;
public interface CtContinueAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtContinue> extends SpoonAssert<A, W> , CtLabelledFlowBreakAssertInterface<A, W> {}
