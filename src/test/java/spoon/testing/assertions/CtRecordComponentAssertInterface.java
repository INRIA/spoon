package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtRecordComponent;
public interface CtRecordComponentAssertInterface<A extends AbstractObjectAssert<A, W>, W extends CtRecordComponent> extends SpoonAssert<A, W> , CtNamedElementAssertInterface<A, W> , CtTypedElementAssertInterface<A, W> , CtShadowableAssertInterface<A, W> {}
