package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtElement;
public class CtElementAssert extends AbstractAssert<CtElementAssert, CtElement> {
	public CtElementAssert(CtElement actual) {
		super(actual, CtElementAssert.class);
	}
}
