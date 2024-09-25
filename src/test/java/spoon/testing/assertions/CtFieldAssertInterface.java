package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtField;
public interface CtFieldAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtField<?>> extends CtVariableAssertInterface<A, W> , SpoonAssert<A, W> , CtRHSReceiverAssertInterface<A, W> , CtShadowableAssertInterface<A, W> , CtTypeMemberAssertInterface<A, W> {}
