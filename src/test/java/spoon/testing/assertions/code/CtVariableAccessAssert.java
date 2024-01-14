package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtVariableAccess;
public class CtVariableAccessAssert extends AbstractAssert<CtVariableAccessAssert, CtVariableAccess> {
	public CtVariableAccessAssert(CtVariableAccess actual) {
		super(actual, CtVariableAccessAssert.class);
	}
}
