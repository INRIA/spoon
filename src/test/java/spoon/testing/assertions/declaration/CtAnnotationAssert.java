package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtAnnotation;
public class CtAnnotationAssert extends AbstractAssert<CtAnnotationAssert, CtAnnotation> {
	public CtAnnotationAssert(CtAnnotation actual) {
		super(actual, CtAnnotationAssert.class);
	}
}
