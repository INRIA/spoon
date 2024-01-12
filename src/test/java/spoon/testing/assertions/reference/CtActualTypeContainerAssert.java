package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.reference.CtActualTypeContainer;
public class CtActualTypeContainerAssert extends AbstractAssert<CtActualTypeContainerAssert, CtActualTypeContainer> {
    CtActualTypeContainerAssert(CtActualTypeContainer actual) {
        super(actual, CtActualTypeContainerAssert.class);
    }
}