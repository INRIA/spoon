package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtAnnotationType;
public interface CtAnnotationTypeAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtAnnotationType<?>> extends SpoonAssert<A, W> , CtTypeAssertInterface<A, W> {}
