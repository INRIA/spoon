package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtVariableWrite;
interface CtVariableWriteAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtVariableWrite<?>> extends CtVariableAccessAssertInterface<A, W> , SpoonAssert<A, W> {}