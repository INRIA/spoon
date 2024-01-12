package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtAnnotatedElementType;
public class CtAnnotatedElementTypeAssert extends AbstractAssert<CtAnnotatedElementTypeAssert, CtAnnotatedElementType> {
    CtAnnotatedElementTypeAssert(CtAnnotatedElementType actual) {
        super(actual, CtAnnotatedElementTypeAssert.class);
    }
}