package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtFieldAccess;
public class CtFieldAccessAssert extends AbstractAssert<CtFieldAccessAssert, CtFieldAccess> {
	public CtFieldAccessAssert(CtFieldAccess actual) {
		super(actual, CtFieldAccessAssert.class);
	}
}
