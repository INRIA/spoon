package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtLocalVariable;
public class CtLocalVariableAssert extends AbstractAssert<CtLocalVariableAssert, CtLocalVariable> {
	public CtLocalVariableAssert(CtLocalVariable actual) {
		super(actual, CtLocalVariableAssert.class);
	}
}
