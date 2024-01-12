package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.LiteralBase;
public class LiteralBaseAssert extends AbstractAssert<LiteralBaseAssert, LiteralBase> {
    LiteralBaseAssert(LiteralBase actual) {
        super(actual, LiteralBaseAssert.class);
    }
}