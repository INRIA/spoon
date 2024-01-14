package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtTypeParameter;
public class CtTypeParameterAssert extends AbstractAssert<CtTypeParameterAssert, CtTypeParameter> {
	public CtTypeParameterAssert(CtTypeParameter actual) {
		super(actual, CtTypeParameterAssert.class);
	}
}
