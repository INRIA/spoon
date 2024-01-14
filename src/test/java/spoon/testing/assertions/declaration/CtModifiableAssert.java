package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtModifiable;
public class CtModifiableAssert extends AbstractAssert<CtModifiableAssert, CtModifiable> {
	public CtModifiableAssert(CtModifiable actual) {
		super(actual, CtModifiableAssert.class);
	}
}
