package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtEnumValue;
public interface CtEnumValueAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtEnumValue<?>> extends SpoonAssert<A, W> , CtFieldAssertInterface<A, W> {}
