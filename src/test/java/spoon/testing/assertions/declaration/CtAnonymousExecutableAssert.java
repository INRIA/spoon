package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtElement;
public class CtAnonymousExecutableAssert extends AbstractAssert<CtAnonymousExecutableAssert, CtAnonymousExecutable> {
	public CtAnonymousExecutableAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtAnonymousExecutableAssert(CtAnonymousExecutable actual) {
		super(actual, CtAnonymousExecutableAssert.class);
	}
}
