package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtResource;
public class CtResourceAssert extends AbstractAssert<CtResourceAssert, CtResource> {
	public CtResourceAssert(CtResource actual) {
		super(actual, CtResourceAssert.class);
	}
}
