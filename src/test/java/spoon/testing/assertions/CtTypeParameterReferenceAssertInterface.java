package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtTypeParameterReference;
public interface CtTypeParameterReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtTypeParameterReference> extends CtTypeReferenceAssertInterface<A, W> , SpoonAssert<A, W> {}
