package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtEnum;
public class CtEnumAssert extends AbstractAssert<CtEnumAssert, CtEnum> {
	public CtEnumAssert(CtEnum actual) {
		super(actual, CtEnumAssert.class);
	}
}
