package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtLoop;
public class CtLoopAssert extends AbstractAssert<CtLoopAssert, CtLoop> {
	public CtLoopAssert(CtLoop actual) {
		super(actual, CtLoopAssert.class);
	}
}
