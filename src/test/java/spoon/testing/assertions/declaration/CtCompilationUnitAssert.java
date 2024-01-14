package spoon.testing.assertions.declaration;
import org.assertj.core.api.AbstractAssert;
import spoon.reflect.declaration.CtCompilationUnit;
public class CtCompilationUnitAssert extends AbstractAssert<CtCompilationUnitAssert, CtCompilationUnit> {
	public CtCompilationUnitAssert(CtCompilationUnit actual) {
		super(actual, CtCompilationUnitAssert.class);
	}
}
