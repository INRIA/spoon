package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.reference.CtLocalVariableReference;
public class CtLocalVariableReferenceAssert extends AbstractAssert<CtLocalVariableReferenceAssert, CtLocalVariableReference> {
	public CtLocalVariableReferenceAssert(CtLocalVariableReference actual) {
		super(actual, CtLocalVariableReferenceAssert.class);
	}
}
