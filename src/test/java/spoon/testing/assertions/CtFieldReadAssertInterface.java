package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtFieldRead;
public interface CtFieldReadAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtFieldRead<?>> extends CtVariableReadAssertInterface<A, W> , SpoonAssert<A, W> , CtFieldAccessAssertInterface<A, W> {}
