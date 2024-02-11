package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtVariableRead;
interface CtVariableReadAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtVariableRead<?>> extends CtVariableAccessAssertInterface<A, W> , SpoonAssert<A, W> {}
