package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtBlock;
public class CtBlockAssert extends AbstractAssert<CtBlockAssert, CtBlock> {
	public CtBlockAssert(CtBlock actual) {
		super(actual, CtBlockAssert.class);
	}
}
