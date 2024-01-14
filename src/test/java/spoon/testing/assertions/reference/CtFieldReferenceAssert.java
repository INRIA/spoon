package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.reference.CtFieldReference;
public class CtFieldReferenceAssert extends AbstractAssert<CtFieldReferenceAssert, CtFieldReference> {
	public CtFieldReferenceAssert(CtFieldReference actual) {
		super(actual, CtFieldReferenceAssert.class);
	}
}
