package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtShadowable;
public class CtShadowableAssert extends AbstractAssert<CtShadowableAssert, CtShadowable> {
    CtShadowableAssert(CtShadowable actual) {
        super(actual, CtShadowableAssert.class);
    }
}