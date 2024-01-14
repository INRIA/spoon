package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtNamedElement;
public class CtNamedElementAssert extends AbstractAssert<CtNamedElementAssert, CtNamedElement> {
	public CtNamedElementAssert(CtNamedElement actual) {
		super(actual, CtNamedElementAssert.class);
	}
}
