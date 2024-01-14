package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.reference.CtVariableReference;
public class CtVariableReferenceAssert extends AbstractAssert<CtVariableReferenceAssert, CtVariableReference> {
	public CtVariableReferenceAssert(CtVariableReference actual) {
		super(actual, CtVariableReferenceAssert.class);
	}
}
