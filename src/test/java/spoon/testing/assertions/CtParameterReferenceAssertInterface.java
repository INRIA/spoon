package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtParameterReference;
public interface CtParameterReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtParameterReference<?>> extends CtVariableReferenceAssertInterface<A, W> , SpoonAssert<A, W> {}
