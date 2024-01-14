package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtEnumValue;
public class CtEnumValueAssert extends AbstractAssert<CtEnumValueAssert, CtEnumValue> {
	public CtEnumValueAssert(CtEnumValue actual) {
		super(actual, CtEnumValueAssert.class);
	}
}
