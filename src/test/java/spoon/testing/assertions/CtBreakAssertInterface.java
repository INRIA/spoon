package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtBreak;
public interface CtBreakAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtBreak> extends SpoonAssert<A, W> , CtLabelledFlowBreakAssertInterface<A, W> {}
