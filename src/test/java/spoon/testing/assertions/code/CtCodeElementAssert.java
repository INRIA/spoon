package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtCodeElement;
public class CtCodeElementAssert extends AbstractAssert<CtCodeElementAssert, CtCodeElement> {
	public CtCodeElementAssert(CtCodeElement actual) {
		super(actual, CtCodeElementAssert.class);
	}
}
