package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtTypedElement;
public class CtTypedElementAssert extends AbstractAssert<CtTypedElementAssert, CtTypedElement> {
	public CtTypedElementAssert(CtTypedElement actual) {
		super(actual, CtTypedElementAssert.class);
	}
}
