package spoon.testing.assertions.reference;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.reference.CtModuleReference;
public class CtModuleReferenceAssert extends AbstractAssert<CtModuleReferenceAssert, CtModuleReference> {
	public CtModuleReferenceAssert(CtModuleReference actual) {
		super(actual, CtModuleReferenceAssert.class);
	}
}
