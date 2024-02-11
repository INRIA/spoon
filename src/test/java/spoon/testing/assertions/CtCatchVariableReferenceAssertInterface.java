package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtCatchVariableReference;
interface CtCatchVariableReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtCatchVariableReference<?>> extends CtVariableReferenceAssertInterface<A, W> , SpoonAssert<A, W> {}
