package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtLabelledFlowBreak;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtLabelledFlowBreakAssert extends AbstractAssert<CtLabelledFlowBreakAssert, CtLabelledFlowBreak> {
    public CtLabelledFlowBreakAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtLabelledFlowBreakAssert(CtLabelledFlowBreak actual) {
        super(actual, CtLabelledFlowBreakAssert.class);
    }
}