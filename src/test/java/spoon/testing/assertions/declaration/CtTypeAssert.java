package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtType;
public class CtTypeAssert extends AbstractAssert<CtTypeAssert, CtType> {
	public CtTypeAssert(CtType actual) {
		super(actual, CtTypeAssert.class);
	}
}
