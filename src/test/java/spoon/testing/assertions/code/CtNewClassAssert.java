package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtNewClass;
public class CtNewClassAssert extends AbstractAssert<CtNewClassAssert, CtNewClass> {
	public CtNewClassAssert(CtNewClass actual) {
		super(actual, CtNewClassAssert.class);
	}
}
