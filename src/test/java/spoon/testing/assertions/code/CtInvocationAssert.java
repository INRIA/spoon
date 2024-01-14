package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtInvocation;
public class CtInvocationAssert extends AbstractAssert<CtInvocationAssert, CtInvocation> {
	public CtInvocationAssert(CtInvocation actual) {
		super(actual, CtInvocationAssert.class);
	}
}
