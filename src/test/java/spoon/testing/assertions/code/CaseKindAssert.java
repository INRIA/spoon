package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CaseKind;
public class CaseKindAssert extends AbstractAssert<CaseKindAssert, CaseKind> {
    CaseKindAssert(CaseKind actual) {
        super(actual, CaseKindAssert.class);
    }
}