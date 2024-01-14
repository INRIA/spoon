package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
public class CtAnnotationAssert extends AbstractAssert<CtAnnotationAssert, CtAnnotation> {
	public CtAnnotationAssert hasParent(CtElement parent) {
		isNotNull();
		if (!super.actual.getParent().equals(parent)) {
			failWithMessage("Expected parent to be <%s> but was <%s>", parent, super.actual.getParent());
		}
		return this;
	}

	public CtAnnotationAssert(CtAnnotation actual) {
		super(actual, CtAnnotationAssert.class);
	}
}
