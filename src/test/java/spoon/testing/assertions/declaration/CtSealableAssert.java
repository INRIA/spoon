package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtSealable;
public class CtSealableAssert extends AbstractAssert<CtSealableAssert, CtSealable> {
	public CtSealableAssert(CtSealable actual) {
		super(actual, CtSealableAssert.class);
	}
}
