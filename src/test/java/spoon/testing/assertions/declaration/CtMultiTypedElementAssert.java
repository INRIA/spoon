package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtMultiTypedElement;
public class CtMultiTypedElementAssert extends AbstractAssert<CtMultiTypedElementAssert, CtMultiTypedElement> {
	public CtMultiTypedElementAssert(CtMultiTypedElement actual) {
		super(actual, CtMultiTypedElementAssert.class);
	}
}
