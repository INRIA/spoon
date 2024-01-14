package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtPackageDeclaration;
public class CtPackageDeclarationAssert extends AbstractAssert<CtPackageDeclarationAssert, CtPackageDeclaration> {
	public CtPackageDeclarationAssert(CtPackageDeclaration actual) {
		super(actual, CtPackageDeclarationAssert.class);
	}
}
