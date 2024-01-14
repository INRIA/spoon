package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtConstructor;
public class CtConstructorAssert extends AbstractAssert<CtConstructorAssert, CtConstructor> {
	public CtConstructorAssert(CtConstructor actual) {
		super(actual, CtConstructorAssert.class);
	}
}
