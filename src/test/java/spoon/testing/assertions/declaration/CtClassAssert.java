package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtClass;
public class CtClassAssert extends AbstractAssert<CtClassAssert, CtClass> {
	public CtClassAssert(CtClass actual) {
		super(actual, CtClassAssert.class);
	}
}
