package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtAnnotationType;
public class CtAnnotationTypeAssert extends AbstractAssert<CtAnnotationTypeAssert, CtAnnotationType> {
	public CtAnnotationTypeAssert(CtAnnotationType actual) {
		super(actual, CtAnnotationTypeAssert.class);
	}
}
