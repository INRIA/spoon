package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtInterface;
public class CtInterfaceAssert extends AbstractAssert<CtInterfaceAssert, CtInterface> {
	public CtInterfaceAssert(CtInterface actual) {
		super(actual, CtInterfaceAssert.class);
	}
}
