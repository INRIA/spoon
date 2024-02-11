package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtThisAccess;
interface CtThisAccessAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtThisAccess<?>> extends SpoonAssert<A, W> , CtTargetedExpressionAssertInterface<A, W> {}
