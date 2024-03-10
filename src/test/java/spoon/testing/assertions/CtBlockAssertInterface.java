package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtBlock;
public interface CtBlockAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtBlock<?>> extends SpoonAssert<A, W> , CtStatementAssertInterface<A, W> , CtStatementListAssertInterface<A, W> {}
