package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtTextBlock;
public class CtTextBlockAssert extends AbstractAssert<CtTextBlockAssert, CtTextBlock> {
	public CtTextBlockAssert(CtTextBlock actual) {
		super(actual, CtTextBlockAssert.class);
	}
}
