package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.reference.CtUnboundVariableReference;
public class CtUnboundVariableReferenceAssert extends AbstractAssert<CtUnboundVariableReferenceAssert, CtUnboundVariableReference> {
	public CtUnboundVariableReferenceAssert(CtUnboundVariableReference actual) {
		super(actual, CtUnboundVariableReferenceAssert.class);
	}
}
