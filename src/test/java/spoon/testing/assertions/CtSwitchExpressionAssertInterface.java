package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtSwitchExpression;
public interface CtSwitchExpressionAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtSwitchExpression<?, ?>> extends SpoonAssert<A, W> , CtExpressionAssertInterface<A, W> , CtAbstractSwitchAssertInterface<A, W> {}
