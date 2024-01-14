package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtField;
public class CtFieldAssert extends AbstractAssert<CtFieldAssert, CtField> {
	public CtFieldAssert(CtField actual) {
		super(actual, CtFieldAssert.class);
	}
}
