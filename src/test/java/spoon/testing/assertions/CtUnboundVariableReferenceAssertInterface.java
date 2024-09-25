package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtUnboundVariableReference;
public interface CtUnboundVariableReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtUnboundVariableReference<?>> extends CtVariableReferenceAssertInterface<A, W> , SpoonAssert<A, W> {}
