package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.declaration.CtElement;
public class CtJavaDocTagAssert extends AbstractAssert<CtJavaDocTagAssert, CtJavaDocTag> {
	public CtJavaDocTagAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtJavaDocTagAssert(CtJavaDocTag actual) {
		super(actual, CtJavaDocTagAssert.class);
	}
}
