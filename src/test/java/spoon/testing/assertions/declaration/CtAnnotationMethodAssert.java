package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtAnnotationMethod;
public class CtAnnotationMethodAssert extends AbstractAssert<CtAnnotationMethodAssert, CtAnnotationMethod> {
	public CtAnnotationMethodAssert(CtAnnotationMethod actual) {
		super(actual, CtAnnotationMethodAssert.class);
	}
}
