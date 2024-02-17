package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtFieldWrite;
public interface CtFieldWriteAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtFieldWrite<?>> extends SpoonAssert<A, W> , CtVariableWriteAssertInterface<A, W> , CtFieldAccessAssertInterface<A, W> {}
