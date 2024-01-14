package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtAbstractInvocation;
public class CtAbstractInvocationAssert extends AbstractAssert<CtAbstractInvocationAssert, CtAbstractInvocation> {
	public CtAbstractInvocationAssert(CtAbstractInvocation actual) {
		super(actual, CtAbstractInvocationAssert.class);
	}
}
