package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtCFlowBreak;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtCFlowBreakAssert extends AbstractAssert<CtCFlowBreakAssert, CtCFlowBreak> {
    public CtCFlowBreakAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtCFlowBreakAssert(CtCFlowBreak actual) {
        super(actual, CtCFlowBreakAssert.class);
    }
}