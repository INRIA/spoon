package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.reference.CtTypeReference;
public class CtTypeReferenceAssert extends AbstractAssert<CtTypeReferenceAssert, CtTypeReference> {
	public CtTypeReferenceAssert(CtTypeReference actual) {
		super(actual, CtTypeReferenceAssert.class);
	}
}
