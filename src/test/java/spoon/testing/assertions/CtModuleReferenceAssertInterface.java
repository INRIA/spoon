package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.reference.CtModuleReference;
public interface CtModuleReferenceAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtModuleReference> extends SpoonAssert<A, W> , CtReferenceAssertInterface<A, W> {}
