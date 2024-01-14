package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.reference.CtTypeParameterReference;
public class CtTypeParameterReferenceAssert extends AbstractAssert<CtTypeParameterReferenceAssert, CtTypeParameterReference> {
	public CtTypeParameterReferenceAssert(CtTypeParameterReference actual) {
		super(actual, CtTypeParameterReferenceAssert.class);
	}
}
