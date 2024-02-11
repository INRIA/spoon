package spoon.testing.assertions;
import org.assertj.core.api.AbstractObjectAssert;
import spoon.reflect.declaration.CtCompilationUnit;
public class CtCompilationUnitAssert extends AbstractObjectAssert<CtCompilationUnitAssert, CtCompilationUnit> implements CtCompilationUnitAssertInterface<CtCompilationUnitAssert, CtCompilationUnit> {
	CtCompilationUnitAssert(CtCompilationUnit actual) {
		super(actual, CtCompilationUnitAssert.class);
	}

	@Override
	public CtCompilationUnitAssert self() {
		return this;
	}

	@Override
	public CtCompilationUnit actual() {
		return this.actual;
	}

	@Override
	public void failWithMessage(String errorMessage, Object... arguments) {
		super.failWithMessage(errorMessage, arguments);
	}
}
