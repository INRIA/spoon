package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtTypeParameter;
public interface CtTypeParameterAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtTypeParameter> extends SpoonAssert<A, W> , CtTypeAssertInterface<A, W> {}
