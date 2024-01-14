package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtParameter;
public class CtParameterAssert extends AbstractAssert<CtParameterAssert, CtParameter> {
	public CtParameterAssert(CtParameter actual) {
		super(actual, CtParameterAssert.class);
	}
}
