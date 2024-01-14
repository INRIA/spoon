package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtElement;
public class CtAnnotationTypeAssert extends AbstractAssert<CtAnnotationTypeAssert, CtAnnotationType> {
	public CtAnnotationTypeAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtAnnotationTypeAssert(CtAnnotationType actual) {
		super(actual, CtAnnotationTypeAssert.class);
	}
}
