package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtCodeElement;
interface CtCodeElementAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtCodeElement> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {}
