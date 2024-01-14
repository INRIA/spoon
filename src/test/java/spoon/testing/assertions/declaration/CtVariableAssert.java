package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtVariable;
public class CtVariableAssert extends AbstractAssert<CtVariableAssert, CtVariable> {
	public CtVariableAssert(CtVariable actual) {
		super(actual, CtVariableAssert.class);
	}
}
