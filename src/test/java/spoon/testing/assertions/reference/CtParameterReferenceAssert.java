package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.reference.CtParameterReference;
public class CtParameterReferenceAssert extends AbstractAssert<CtParameterReferenceAssert, CtParameterReference> {
	public CtParameterReferenceAssert(CtParameterReference actual) {
		super(actual, CtParameterReferenceAssert.class);
	}
}
