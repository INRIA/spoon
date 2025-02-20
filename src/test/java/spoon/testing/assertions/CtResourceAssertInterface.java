package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.code.CtResource;
public interface CtResourceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtResource<?>> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> {}
