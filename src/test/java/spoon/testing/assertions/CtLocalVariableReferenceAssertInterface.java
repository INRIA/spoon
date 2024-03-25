package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtLocalVariableReference;
public interface CtLocalVariableReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtLocalVariableReference<?>> extends CtVariableReferenceAssertInterface<A, W> , SpoonAssert<A, W> {}
