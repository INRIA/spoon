package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.reference.CtExecutableReference;
public class CtExecutableReferenceAssert extends AbstractAssert<CtExecutableReferenceAssert, CtExecutableReference> {
	public CtExecutableReferenceAssert(CtExecutableReference actual) {
		super(actual, CtExecutableReferenceAssert.class);
	}
}
