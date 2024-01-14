package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.ParentNotInitializedException;
public class ParentNotInitializedExceptionAssert extends AbstractAssert<ParentNotInitializedExceptionAssert, ParentNotInitializedException> {
	public ParentNotInitializedExceptionAssert(ParentNotInitializedException actual) {
		super(actual, ParentNotInitializedExceptionAssert.class);
	}
}
