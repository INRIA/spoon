package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtMethod;
public class CtMethodAssert extends AbstractAssert<CtMethodAssert, CtMethod> {
	public CtMethodAssert(CtMethod actual) {
		super(actual, CtMethodAssert.class);
	}
}
