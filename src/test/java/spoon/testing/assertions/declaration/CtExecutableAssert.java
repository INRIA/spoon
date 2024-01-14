package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtExecutable;
public class CtExecutableAssert extends AbstractAssert<CtExecutableAssert, CtExecutable> {
	public CtExecutableAssert(CtExecutable actual) {
		super(actual, CtExecutableAssert.class);
	}
}
