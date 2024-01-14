package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.reference.CtPackageReference;
public class CtPackageReferenceAssert extends AbstractAssert<CtPackageReferenceAssert, CtPackageReference> {
	public CtPackageReferenceAssert(CtPackageReference actual) {
		super(actual, CtPackageReferenceAssert.class);
	}
}
