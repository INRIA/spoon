package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtArrayAccess;
public class CtArrayAccessAssert extends AbstractAssert<CtArrayAccessAssert, CtArrayAccess> {
	public CtArrayAccessAssert(CtArrayAccess actual) {
		super(actual, CtArrayAccessAssert.class);
	}
}
