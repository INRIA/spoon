package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtReceiverParameter;
public interface CtReceiverParameterAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtReceiverParameter> extends CtElementAssertInterface<A, W> , SpoonAssert<A, W> , CtTypedElementAssertInterface<A, W> , CtShadowableAssertInterface<A, W> {}
