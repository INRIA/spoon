package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtTypeInformation;
public class CtTypeInformationAssert extends AbstractAssert<CtTypeInformationAssert, CtTypeInformation> {
    CtTypeInformationAssert(CtTypeInformation actual) {
        super(actual, CtTypeInformationAssert.class);
    }
}