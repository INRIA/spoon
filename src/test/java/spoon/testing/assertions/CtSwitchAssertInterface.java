package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtSwitch;
public interface CtSwitchAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtSwitch<?>> extends SpoonAssert<A, W> , CtStatementAssertInterface<A, W> , CtAbstractSwitchAssertInterface<A, W> {}
