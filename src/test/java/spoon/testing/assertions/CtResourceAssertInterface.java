package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtResource;
interface CtResourceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtResource<?>> extends CtVariableAssertInterface<A, W> , SpoonAssert<A, W> {}
