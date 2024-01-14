package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtPackage;
public class CtPackageAssert extends AbstractAssert<CtPackageAssert, CtPackage> {
	public CtPackageAssert(CtPackage actual) {
		super(actual, CtPackageAssert.class);
	}
}
