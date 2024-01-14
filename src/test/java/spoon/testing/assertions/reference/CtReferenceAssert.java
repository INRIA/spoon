package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.reference.CtReference;
public class CtReferenceAssert extends AbstractAssert<CtReferenceAssert, CtReference> {
	public CtReferenceAssert(CtReference actual) {
		super(actual, CtReferenceAssert.class);
	}
}
