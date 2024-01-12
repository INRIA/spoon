package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.declaration.CtElement;
import spoon.testing.assertions.declaration.CtElementAssert;
public class CtArrayWriteAssert extends AbstractAssert<CtArrayWriteAssert, CtArrayWrite> {
    public CtArrayWriteAssert hasParent(CtElement parent) {
        isNotNull();
        if (!super.actual.getParent().equals(parent)) {
            failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
        }
        return this;
    }

    CtArrayWriteAssert(CtArrayWrite actual) {
        super(actual, CtArrayWriteAssert.class);
    }
}