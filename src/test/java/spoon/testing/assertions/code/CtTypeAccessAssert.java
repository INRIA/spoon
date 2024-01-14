package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtTypeAccess;
public class CtTypeAccessAssert extends AbstractAssert<CtTypeAccessAssert, CtTypeAccess> {
	public CtTypeAccessAssert(CtTypeAccess actual) {
		super(actual, CtTypeAccessAssert.class);
	}
}
