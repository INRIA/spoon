package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtArrayWrite;
interface CtArrayWriteAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtArrayWrite<?>> extends SpoonAssert<A, W> , CtArrayAccessAssertInterface<A, W> {}