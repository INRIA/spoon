package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.reference.CtArrayTypeReference;
public class CtArrayTypeReferenceAssert extends AbstractAssert<CtArrayTypeReferenceAssert, CtArrayTypeReference> {
	public CtArrayTypeReferenceAssert(CtArrayTypeReference actual) {
		super(actual, CtArrayTypeReferenceAssert.class);
	}
}
