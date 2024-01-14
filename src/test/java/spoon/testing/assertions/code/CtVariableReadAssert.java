package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtVariableRead;
public class CtVariableReadAssert extends AbstractAssert<CtVariableReadAssert, CtVariableRead> {
	public CtVariableReadAssert(CtVariableRead actual) {
		super(actual, CtVariableReadAssert.class);
	}
}
