package spoon.testing.assertions.code;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.code.CtJavaDoc;
public class CtJavaDocAssert extends AbstractAssert<CtJavaDocAssert, CtJavaDoc> {
	public CtJavaDocAssert(CtJavaDoc actual) {
		super(actual, CtJavaDocAssert.class);
	}
}
