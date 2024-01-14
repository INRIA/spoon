package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtThrow;
public class CtThrowAssert extends AbstractAssert<CtThrowAssert, CtThrow> {
	public CtThrowAssert(CtThrow actual) {
		super(actual, CtThrowAssert.class);
	}
}
