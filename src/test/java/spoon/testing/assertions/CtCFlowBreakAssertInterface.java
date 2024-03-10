package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtCFlowBreak;
public interface CtCFlowBreakAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtCFlowBreak> extends SpoonAssert<A, W> , CtStatementAssertInterface<A, W> {}
