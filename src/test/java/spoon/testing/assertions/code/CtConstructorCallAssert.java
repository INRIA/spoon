package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtConstructorCall;
public class CtConstructorCallAssert extends AbstractAssert<CtConstructorCallAssert, CtConstructorCall> {
	public CtConstructorCallAssert(CtConstructorCall actual) {
		super(actual, CtConstructorCallAssert.class);
	}
}
